import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../../api/client.js'
import { salaryRange, timeAgo } from '../../../shared/format.js'
import { Alert, Button, Card, Empty, PageTitle, Spinner, StatusBadge } from '../../../shared/components/ui.jsx'

export default function MyJobsPage() {
  const [jobs, setJobs] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api('/recruiter/jobs')
      .then(setJobs)
      .catch((e) => setError(e.message))
  }, [])

  const toggleStatus = async (job) => {
    try {
      const updated = await api(`/recruiter/jobs/${job.id}/status`, {
        method: 'PATCH',
        body: { status: job.status === 'OPEN' ? 'CLOSED' : 'OPEN' },
      })
      setJobs((list) => list.map((j) => (j.id === job.id ? updated : j)))
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <div>
      <PageTitle title="My job postings">
        <Link to="/recruiter/jobs/new" className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700">
          + Post a job
        </Link>
      </PageTitle>
      <Alert>{error}</Alert>
      {!jobs && !error && <Spinner />}
      {jobs && jobs.length === 0 && <Empty>You haven't posted any jobs yet.</Empty>}
      <div className="space-y-3">
        {jobs?.map((job) => (
          <Card key={job.id} data-testid="recruiter-job">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <div>
                <h2 className="text-lg font-semibold text-slate-900">
                  {job.title} <StatusBadge status={job.status} />
                </h2>
                <p className="text-sm text-slate-600">
                  {job.companyName} · {job.location}
                  {job.remote ? ' · Remote OK' : ''} · {salaryRange(job.salaryMin, job.salaryMax)} · posted {timeAgo(job.postedAt)}
                </p>
              </div>
              <div className="flex flex-wrap items-center gap-2">
                <Link to={`/recruiter/jobs/${job.id}/applicants`} className="rounded-lg bg-indigo-50 px-3 py-2 text-sm font-medium text-indigo-700 hover:bg-indigo-100">
                  Applicants ({job.applicantCount})
                </Link>
                <Link to={`/recruiter/jobs/${job.id}/edit`} className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50">
                  Edit
                </Link>
                <Button variant="secondary" onClick={() => toggleStatus(job)}>
                  {job.status === 'OPEN' ? 'Close' : 'Reopen'}
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  )
}
