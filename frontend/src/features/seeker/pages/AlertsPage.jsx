import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../../../api/client.js'
import { toLpa } from '../../../shared/format.js'
import { Alert, Button, Card, Empty, PageTitle, SkillTag, Spinner } from '../../../shared/components/ui.jsx'

export default function AlertsPage() {
  const [alerts, setAlerts] = useState(null)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    api('/seeker/alerts')
      .then(setAlerts)
      .catch((e) => setError(e.message))
  }, [])

  const view = async (a) => {
    // open the search that this alert saved, and reset its "new" counter
    const p = new URLSearchParams()
    if (a.q) p.set('q', a.q)
    if (a.location) p.set('location', a.location)
    if (a.remote) p.set('remote', 'true')
    if (a.minSalary) p.set('minLpa', toLpa(a.minSalary))
    if (a.skills.length) p.set('skills', a.skills.join(','))
    await api(`/seeker/alerts/${a.id}/seen`, { method: 'POST' }).catch(() => {})
    navigate(`/seeker/jobs?${p}`)
  }

  const remove = async (a) => {
    try {
      await api(`/seeker/alerts/${a.id}`, { method: 'DELETE' })
      setAlerts((list) => list.filter((x) => x.id !== a.id))
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <div className="mx-auto max-w-3xl">
      <PageTitle title="Job alerts" />
      <p className="mb-4 text-sm text-slate-600">
        An alert remembers a search and tells you when new matching jobs are posted. Create one from the{' '}
        <Link to="/seeker/jobs" className="text-indigo-600 hover:underline">
          job search
        </Link>{' '}
        page.
      </p>
      <Alert>{error}</Alert>
      {!alerts && !error && <Spinner />}
      {alerts && alerts.length === 0 && <Empty>No alerts yet. Search for jobs, then click "Create alert for this search".</Empty>}
      <div className="space-y-4">
        {alerts?.map((a) => (
          <Card key={a.id} data-testid="alert-card">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <div>
                <h2 className="font-semibold text-slate-900">
                  {a.name}{' '}
                  {a.newCount > 0 && (
                    <span className="ml-1 rounded-full bg-green-100 px-2 py-0.5 text-xs font-semibold text-green-700">{a.newCount} new</span>
                  )}
                </h2>
                <div className="mt-2 flex flex-wrap gap-2 text-xs text-slate-600">
                  {a.q && <span className="rounded bg-slate-100 px-2 py-0.5">“{a.q}”</span>}
                  {a.location && <span className="rounded bg-slate-100 px-2 py-0.5">📍 {a.location}</span>}
                  {a.remote && <span className="rounded bg-slate-100 px-2 py-0.5">Remote</span>}
                  {a.minSalary && <span className="rounded bg-slate-100 px-2 py-0.5">₹{toLpa(a.minSalary)}+ LPA</span>}
                  {a.skills.map((s) => (
                    <SkillTag key={s}>{s}</SkillTag>
                  ))}
                </div>
              </div>
              <div className="flex gap-2">
                <Button onClick={() => view(a)}>View matches</Button>
                <Button variant="danger" onClick={() => remove(a)}>
                  Delete
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  )
}
