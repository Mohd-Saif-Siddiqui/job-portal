import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../../../api/client.js'
import { salaryRange, timeAgo } from '../../../shared/format.js'
import { Alert, Button, Card, SkillTag, Spinner, StatusBadge, inputClass } from '../../../shared/components/ui.jsx'

export default function JobDetailPage() {
  const { id } = useParams()
  const [job, setJob] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [coverNote, setCoverNote] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    api(`/seeker/jobs/${id}`)
      .then(setJob)
      .catch((e) => setError(e.message))
  }, [id])

  const toggleSave = async () => {
    try {
      await api(`/seeker/jobs/${job.id}/save`, { method: job.saved ? 'DELETE' : 'PUT' })
      setJob({ ...job, saved: !job.saved })
    } catch (e) {
      setError(e.message)
    }
  }

  const apply = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const a = await api('/seeker/applications', { method: 'POST', body: { jobId: job.id, coverNote } })
      setJob({ ...job, applicationStatus: a.status })
      setNotice('Application submitted! Track it under "My applications".')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  if (error && !job) return <Alert>{error}</Alert>
  if (!job) return <Spinner />

  return (
    <div className="mx-auto max-w-3xl space-y-4">
      <Link to="/seeker/jobs" className="text-sm text-indigo-600 hover:underline">
        ← Back to jobs
      </Link>
      <Card>
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 className="text-2xl font-bold text-slate-900">{job.title}</h1>
            <p className="text-slate-600">{job.companyName}</p>
          </div>
          <Button variant="secondary" onClick={toggleSave} aria-pressed={job.saved}>
            {job.saved ? '★ Saved' : '☆ Save job'}
          </Button>
        </div>
        <div className="mt-3 flex flex-wrap gap-x-5 gap-y-1 text-sm text-slate-600">
          <span>📍 {job.location}</span>
          {job.remote && <span className="font-medium text-green-700">Remote OK</span>}
          <span>💰 {salaryRange(job.salaryMin, job.salaryMax)}</span>
          <span className="text-slate-400">Posted {timeAgo(job.postedAt)}</span>
        </div>
        <div className="mt-3 flex flex-wrap gap-2">
          {job.skills.map((s) => (
            <SkillTag key={s}>{s}</SkillTag>
          ))}
        </div>
        <h2 className="mb-1 mt-6 font-semibold text-slate-900">About the job</h2>
        <p className="whitespace-pre-line text-sm leading-6 text-slate-700">{job.description}</p>
      </Card>

      <Alert>{error}</Alert>
      <Alert kind="success">{notice}</Alert>

      <Card>
        {job.applicationStatus ? (
          <p className="flex items-center gap-2 text-sm text-slate-700">
            You applied to this job. Status: <StatusBadge status={job.applicationStatus} />
          </p>
        ) : job.status !== 'OPEN' ? (
          <p className="text-sm text-slate-600">This job is no longer accepting applications.</p>
        ) : (
          <form onSubmit={apply} className="space-y-3">
            <h2 className="font-semibold text-slate-900">Apply for this job</h2>
            <textarea
              value={coverNote}
              onChange={(e) => setCoverNote(e.target.value)}
              maxLength={2000}
              rows={4}
              placeholder="Cover note (optional): tell the recruiter why you're a good fit"
              className={inputClass}
            />
            <p className="text-xs text-slate-500">
              Your profile and resume are shared with the recruiter.{' '}
              <Link to="/seeker/profile" className="text-indigo-600 hover:underline">
                Update your profile
              </Link>
            </p>
            <Button type="submit" disabled={busy}>
              {busy ? 'Submitting…' : 'Apply now'}
            </Button>
          </form>
        )}
      </Card>
    </div>
  )
}
