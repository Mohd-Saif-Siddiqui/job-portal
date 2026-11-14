import { useEffect, useState } from 'react'
import { api } from '../../../api/client.js'
import { Alert, Empty, PageTitle, Spinner } from '../../../shared/components/ui.jsx'
import JobCard from '../components/JobCard.jsx'

export default function SavedJobsPage() {
  const [jobs, setJobs] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api('/seeker/jobs/saved')
      .then(setJobs)
      .catch((e) => setError(e.message))
  }, [])

  const unsave = async (job) => {
    try {
      await api(`/seeker/jobs/${job.id}/save`, { method: 'DELETE' })
      setJobs((list) => list.filter((j) => j.id !== job.id))
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <div className="mx-auto max-w-3xl">
      <PageTitle title="Saved jobs" />
      <Alert>{error}</Alert>
      {!jobs && !error && <Spinner />}
      {jobs && jobs.length === 0 && <Empty>You haven't saved any jobs yet. Click ☆ Save on a job to keep it here.</Empty>}
      <div className="space-y-4">
        {jobs?.map((job) => (
          <JobCard key={job.id} job={job} onToggleSave={unsave} />
        ))}
      </div>
    </div>
  )
}
