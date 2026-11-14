import { Link } from 'react-router-dom'
import { salaryRange, timeAgo } from '../../../shared/format.js'
import { Card, SkillTag, StatusBadge } from '../../../shared/components/ui.jsx'

export default function JobCard({ job, onToggleSave }) {
  return (
    <Card className="transition-shadow hover:shadow-md" data-testid="job-card">
      <div className="flex items-start justify-between gap-3">
        <div>
          <Link to={`/seeker/jobs/${job.id}`} className="text-lg font-semibold text-slate-900 hover:text-indigo-600">
            {job.title}
          </Link>
          <p className="text-sm text-slate-600">{job.companyName}</p>
        </div>
        <button
          onClick={() => onToggleSave(job)}
          aria-label={job.saved ? 'Unsave job' : 'Save job'}
          aria-pressed={job.saved}
          title={job.saved ? 'Remove from saved' : 'Save for later'}
          className={`rounded-lg border px-2.5 py-1 text-sm ${
            job.saved ? 'border-indigo-200 bg-indigo-50 text-indigo-700' : 'border-slate-200 text-slate-500 hover:bg-slate-50'
          }`}
        >
          {job.saved ? '★ Saved' : '☆ Save'}
        </button>
      </div>
      <div className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-slate-600">
        <span>📍 {job.location}</span>
        {job.remote && <span className="font-medium text-green-700">Remote OK</span>}
        <span>💰 {salaryRange(job.salaryMin, job.salaryMax)}</span>
        <span className="text-slate-400">{timeAgo(job.postedAt)}</span>
      </div>
      <div className="mt-3 flex flex-wrap items-center gap-2">
        {job.skills.map((s) => (
          <SkillTag key={s}>{s}</SkillTag>
        ))}
        {job.applicationStatus && (
          <span className="ml-auto flex items-center gap-1 text-xs text-slate-500">
            Your application: <StatusBadge status={job.applicationStatus} />
          </span>
        )}
      </div>
    </Card>
  )
}
