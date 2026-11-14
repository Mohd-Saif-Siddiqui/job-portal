import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../../../api/client.js'
import { ALL_STATUSES, STATUS_LABEL, timeAgo, toLpa } from '../../../shared/format.js'
import { Alert, Empty, SkillTag, Spinner } from '../../../shared/components/ui.jsx'

const columnStyle = {
  APPLIED: 'border-blue-200',
  SHORTLISTED: 'border-amber-200',
  INTERVIEW: 'border-purple-200',
  OFFER: 'border-green-200',
  REJECTED: 'border-red-200',
}

/** Applicant board: one column per stage. Use the dropdown on a card to move it to another stage. */
export default function ApplicantsPage() {
  const { id } = useParams()
  const [job, setJob] = useState(null)
  const [applicants, setApplicants] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([api(`/recruiter/jobs/${id}`), api(`/recruiter/jobs/${id}/applicants`)])
      .then(([j, a]) => {
        setJob(j)
        setApplicants(a)
      })
      .catch((e) => setError(e.message))
  }, [id])

  const move = async (applicant, status) => {
    try {
      const updated = await api(`/recruiter/applications/${applicant.applicationId}/status`, { method: 'PATCH', body: { status } })
      setApplicants((list) => list.map((a) => (a.applicationId === updated.applicationId ? updated : a)))
    } catch (e) {
      setError(e.message)
    }
  }

  if (error && !applicants) return <Alert>{error}</Alert>
  if (!applicants) return <Spinner />

  return (
    <div>
      <Link to="/recruiter/jobs" className="text-sm text-indigo-600 hover:underline">
        ← Back to my jobs
      </Link>
      <h1 className="mb-1 mt-2 text-2xl font-bold text-slate-900">Applicants: {job?.title}</h1>
      <p className="mb-5 text-sm text-slate-600">
        {applicants.length} applicant{applicants.length === 1 ? '' : 's'} · {job?.companyName}
      </p>
      <Alert>{error}</Alert>
      {applicants.length === 0 ? (
        <Empty>No one has applied yet.</Empty>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
          {ALL_STATUSES.map((status) => {
            const column = applicants.filter((a) => a.status === status)
            return (
              <div key={status} className={`rounded-xl border-t-4 bg-slate-100 p-3 ${columnStyle[status]}`} data-testid={`column-${status}`}>
                <h2 className="mb-3 text-sm font-semibold text-slate-700">
                  {STATUS_LABEL[status]} ({column.length})
                </h2>
                <div className="space-y-3">
                  {column.map((a) => (
                    <div key={a.applicationId} className="rounded-lg bg-white p-3 text-sm shadow-sm" data-testid="applicant-card">
                      <p className="font-semibold text-slate-900">{a.fullName}</p>
                      <p className="break-all text-xs text-slate-500">{a.email}</p>
                      {a.headline && <p className="mt-1 text-slate-700">{a.headline}</p>}
                      <p className="mt-1 text-xs text-slate-500">
                        {a.location && `📍 ${a.location} · `}
                        {a.expectedSalary ? `Expects ₹${toLpa(a.expectedSalary)} LPA · ` : ''}applied {timeAgo(a.appliedAt)}
                      </p>
                      {a.skills.length > 0 && (
                        <div className="mt-2 flex flex-wrap gap-1">
                          {a.skills.map((s) => (
                            <SkillTag key={s.name}>
                              {s.name} · {s.yearsExperience}y
                            </SkillTag>
                          ))}
                        </div>
                      )}
                      {a.coverNote && <p className="mt-2 rounded bg-slate-50 p-2 text-xs italic text-slate-600">“{a.coverNote}”</p>}
                      <div className="mt-3 flex items-center gap-2">
                        <select
                          aria-label={`Move ${a.fullName}`}
                          value={a.status}
                          onChange={(e) => move(a, e.target.value)}
                          className="flex-1 rounded border border-slate-300 px-2 py-1 text-xs"
                        >
                          {ALL_STATUSES.map((s) => (
                            <option key={s} value={s}>
                              {STATUS_LABEL[s]}
                            </option>
                          ))}
                        </select>
                        {a.hasResume && (
                          <a href={`/api/recruiter/applications/${a.applicationId}/resume`} className="text-xs text-indigo-600 hover:underline">
                            Resume
                          </a>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
