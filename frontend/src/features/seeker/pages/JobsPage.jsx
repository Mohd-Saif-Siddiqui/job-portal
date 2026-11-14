import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api } from '../../../api/client.js'
import useSkills from '../../../shared/hooks/useSkills.js'
import { Alert, Button, Card, Empty, Field, PageTitle, Spinner, inputClass } from '../../../shared/components/ui.jsx'
import JobCard from '../components/JobCard.jsx'

const SALARY_OPTIONS = [3, 5, 8, 12, 15, 20, 30] // minimum LPA choices
const PAGE_SIZE = 10

/** The filters live in the URL (?q=java&skills=Java,React&page=1) so refresh, back and alert links all work. */
export default function JobsPage() {
  const [params, setParams] = useSearchParams()
  const allSkills = useSkills()
  const q = params.get('q') || ''
  const location = params.get('location') || ''
  const remote = params.get('remote') === 'true'
  const minLpa = params.get('minLpa') || ''
  const skills = params.get('skills') ? params.get('skills').split(',') : []
  const page = Number(params.get('page') || 0)

  const [draftQ, setDraftQ] = useState(q)
  const [draftLocation, setDraftLocation] = useState(location)
  const [skillFilter, setSkillFilter] = useState('')
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [alertName, setAlertName] = useState('')
  const [showAlert, setShowAlert] = useState(false)

  // keep the text boxes in sync when the URL changes (e.g. arriving from an alert)
  useEffect(() => {
    setDraftQ(q)
    setDraftLocation(location)
  }, [q, location])

  const key = params.toString()
  useEffect(() => {
    setError('')
    const query = new URLSearchParams()
    if (q) query.set('q', q)
    if (location) query.set('location', location)
    if (remote) query.set('remote', 'true')
    if (minLpa) query.set('minSalary', String(Number(minLpa) * 100000))
    if (skills.length) query.set('skills', skills.join(','))
    query.set('page', String(page))
    query.set('size', String(PAGE_SIZE))
    api(`/seeker/jobs?${query}`)
      .then(setData)
      .catch((e) => setError(e.message))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key])

  const update = (changes) => {
    const next = new URLSearchParams(params)
    Object.entries(changes).forEach(([k, v]) => {
      if (v === '' || v === false || v == null || (Array.isArray(v) && v.length === 0)) next.delete(k)
      else next.set(k, Array.isArray(v) ? v.join(',') : String(v))
    })
    if (!('page' in changes)) next.delete('page') // any filter change goes back to page 1
    setParams(next)
  }

  const onSearch = (e) => {
    e.preventDefault()
    update({ q: draftQ.trim(), location: draftLocation.trim() })
  }

  const toggleSkill = (s) => update({ skills: skills.includes(s) ? skills.filter((x) => x !== s) : [...skills, s] })

  const clearAll = () => setParams({})

  const toggleSave = async (job) => {
    try {
      await api(`/seeker/jobs/${job.id}/save`, { method: job.saved ? 'DELETE' : 'PUT' })
      setData((d) => ({ ...d, content: d.content.map((j) => (j.id === job.id ? { ...j, saved: !j.saved } : j)) }))
    } catch (e) {
      setError(e.message)
    }
  }

  const createAlert = async (e) => {
    e.preventDefault()
    try {
      await api('/seeker/alerts', {
        method: 'POST',
        body: {
          name: alertName || 'My job alert',
          q: q || null,
          skills,
          location: location || null,
          remote,
          minSalary: minLpa ? Number(minLpa) * 100000 : null,
        },
      })
      setNotice('Alert created! You can see new matches on the Alerts page.')
      setShowAlert(false)
      setAlertName('')
    } catch (err) {
      setError(err.message)
    }
  }

  const hasFilters = q || location || remote || minLpa || skills.length
  const visibleSkills = allSkills.filter((s) => s.toLowerCase().includes(skillFilter.toLowerCase()))

  return (
    <div>
      <PageTitle title="Find jobs" />
      <div className="grid gap-6 lg:grid-cols-[18rem_1fr]">
        <aside>
          <Card className="space-y-4">
            <form onSubmit={onSearch} className="space-y-3">
              <Field label="Keyword">
                <input value={draftQ} onChange={(e) => setDraftQ(e.target.value)} placeholder="Title, company or keyword" className={inputClass} />
              </Field>
              <Field label="Location">
                <input value={draftLocation} onChange={(e) => setDraftLocation(e.target.value)} placeholder="e.g. Pune" className={inputClass} />
              </Field>
              <Button type="submit" className="w-full">
                Search
              </Button>
            </form>

            <label className="flex items-center gap-2 text-sm text-slate-700">
              <input type="checkbox" checked={remote} onChange={(e) => update({ remote: e.target.checked })} />
              Remote jobs only
            </label>

            <Field label="Minimum salary">
              <select value={minLpa} onChange={(e) => update({ minLpa: e.target.value })} className={inputClass}>
                <option value="">Any</option>
                {SALARY_OPTIONS.map((n) => (
                  <option key={n} value={n}>
                    ₹{n} LPA and above
                  </option>
                ))}
              </select>
            </Field>

            <div>
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Skills {skills.length > 0 && <span className="text-indigo-600">({skills.length} selected)</span>}
              </span>
              <input value={skillFilter} onChange={(e) => setSkillFilter(e.target.value)} placeholder="Filter skills…" className={`${inputClass} mb-2`} />
              <div className="flex max-h-48 flex-wrap gap-1.5 overflow-y-auto">
                {visibleSkills.map((s) => (
                  <button
                    key={s}
                    type="button"
                    aria-pressed={skills.includes(s)}
                    onClick={() => toggleSkill(s)}
                    className={`rounded-full border px-2.5 py-0.5 text-xs font-medium ${
                      skills.includes(s) ? 'border-indigo-600 bg-indigo-600 text-white' : 'border-slate-300 text-slate-600 hover:bg-slate-50'
                    }`}
                  >
                    {s}
                  </button>
                ))}
              </div>
            </div>

            {hasFilters && (
              <Button variant="secondary" onClick={clearAll} className="w-full">
                Clear all filters
              </Button>
            )}
          </Card>
        </aside>

        <section className="space-y-4">
          <Alert>{error}</Alert>
          <Alert kind="success">{notice}</Alert>
          {!data && !error && <Spinner />}
          {data && (
            <>
              <div className="flex flex-wrap items-center justify-between gap-2">
                <p className="text-sm text-slate-600" data-testid="result-count">
                  {data.totalElements} job{data.totalElements === 1 ? '' : 's'} found
                </p>
                {hasFilters && !showAlert && (
                  <Button variant="secondary" onClick={() => setShowAlert(true)}>
                    🔔 Create alert for this search
                  </Button>
                )}
              </div>
              {showAlert && (
                <form onSubmit={createAlert} className="flex flex-wrap gap-2 rounded-xl border border-indigo-200 bg-indigo-50 p-3">
                  <input value={alertName} onChange={(e) => setAlertName(e.target.value)} placeholder="Name this alert, e.g. Java jobs in Pune" className={`${inputClass} flex-1`} />
                  <Button type="submit">Save alert</Button>
                  <Button type="button" variant="secondary" onClick={() => setShowAlert(false)}>
                    Cancel
                  </Button>
                </form>
              )}
              {data.content.length === 0 && <Empty>No jobs match these filters. Try removing a filter.</Empty>}
              {data.content.map((job) => (
                <JobCard key={job.id} job={job} onToggleSave={toggleSave} />
              ))}
              {data.totalPages > 1 && (
                <div className="flex items-center justify-center gap-3 pt-2">
                  <Button variant="secondary" disabled={page <= 0} onClick={() => update({ page: page - 1 })}>
                    ← Previous
                  </Button>
                  <span className="text-sm text-slate-600">
                    Page {data.page + 1} of {data.totalPages}
                  </span>
                  <Button variant="secondary" disabled={page + 1 >= data.totalPages} onClick={() => update({ page: page + 1 })}>
                    Next →
                  </Button>
                </div>
              )}
            </>
          )}
        </section>
      </div>
    </div>
  )
}
