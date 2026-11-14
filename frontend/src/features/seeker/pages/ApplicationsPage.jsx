import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../../api/client.js'
import { STAGES, STATUS_LABEL, salaryRange, timeAgo } from '../../../shared/format.js'
import { Alert, Card, Empty, PageTitle, Spinner, StatusBadge } from '../../../shared/components/ui.jsx'

/** Progress bar: Applied → Shortlisted → Interview → Offer. */
function Stepper({ status }) {
  if (status === 'REJECTED') {
    return <p className="text-sm font-medium text-red-600">Not selected for this role</p>
  }
  const current = STAGES.indexOf(status)
  return (
    <ol className="flex items-center gap-2 text-xs" aria-label="Application progress">
      {STAGES.map((stage, i) => (
        <li key={stage} className="flex flex-1 items-center gap-2">
          <span className={`flex h-6 w-6 shrink-0 items-center justify-center rounded-full font-semibold ${i <= current ? 'bg-indigo-600 text-white' : 'bg-slate-200 text-slate-500'}`}>
            {i < current ? '✓' : i + 1}
          </span>
          <span className={i <= current ? 'font-medium text-slate-800' : 'text-slate-400'}>{STATUS_LABEL[stage]}</span>
          {i < STAGES.length - 1 && <span className={`h-0.5 flex-1 ${i < current ? 'bg-indigo-600' : 'bg-slate-200'}`} />}
        </li>
      ))}
    </ol>
  )
}

export default function ApplicationsPage() {
  const [apps, setApps] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api('/seeker/applications')
      .then(setApps)
      .catch((e) => setError(e.message))
  }, [])

  return (
    <div className="mx-auto max-w-3xl">
      <PageTitle title="My applications" />
      <Alert>{error}</Alert>
      {!apps && !error && <Spinner />}
      {apps && apps.length === 0 && (
        <Empty>
          You haven't applied to any jobs yet.{' '}
          <Link to="/seeker/jobs" className="text-indigo-600 hover:underline">
            Browse jobs
          </Link>
        </Empty>
      )}
      <div className="space-y-4">
        {apps?.map((a) => (
          <Card key={a.id} data-testid="application-card">
            <div className="mb-3 flex flex-wrap items-start justify-between gap-2">
              <div>
                <Link to={`/seeker/jobs/${a.jobId}`} className="text-lg font-semibold text-slate-900 hover:text-indigo-600">
                  {a.jobTitle}
                </Link>
                <p className="text-sm text-slate-600">
                  {a.companyName} · {a.location} · {salaryRange(a.salaryMin, a.salaryMax)}
                </p>
              </div>
              <div className="text-right text-xs text-slate-500">
                <StatusBadge status={a.status} />
                <p className="mt-1">Applied {timeAgo(a.appliedAt)}</p>
              </div>
            </div>
            <Stepper status={a.status} />
          </Card>
        ))}
      </div>
    </div>
  )
}
