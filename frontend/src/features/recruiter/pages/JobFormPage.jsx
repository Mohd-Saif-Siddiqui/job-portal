import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../../../api/client.js'
import { lpaToRupees, toLpa } from '../../../shared/format.js'
import useSkills from '../../../shared/hooks/useSkills.js'
import SkillPicker from '../../../shared/components/SkillPicker.jsx'
import { Alert, Button, Card, Field, PageTitle, Spinner, inputClass } from '../../../shared/components/ui.jsx'

const empty = { title: '', companyName: '', location: '', remote: false, minLpa: '', maxLpa: '', description: '', skills: [] }

/** Used for both "Post a job" (/recruiter/jobs/new) and "Edit job" (/recruiter/jobs/:id/edit). */
export default function JobFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const allSkills = useSkills()
  const [form, setForm] = useState(id ? null : empty)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    if (!id) return
    api(`/recruiter/jobs/${id}`)
      .then((j) =>
        setForm({
          title: j.title,
          companyName: j.companyName,
          location: j.location,
          remote: j.remote,
          minLpa: toLpa(j.salaryMin),
          maxLpa: toLpa(j.salaryMax),
          description: j.description,
          skills: j.skills,
        }),
      )
      .catch((e) => setError(e.message))
  }, [id])

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await api(id ? `/recruiter/jobs/${id}` : '/recruiter/jobs', {
        method: id ? 'PUT' : 'POST',
        body: {
          title: form.title,
          companyName: form.companyName,
          location: form.location,
          remote: form.remote,
          salaryMin: lpaToRupees(form.minLpa),
          salaryMax: lpaToRupees(form.maxLpa),
          description: form.description,
          skills: form.skills,
        },
      })
      navigate('/recruiter/jobs')
    } catch (err) {
      setError(err.message)
      setBusy(false)
    }
  }

  if (!form) return error ? <Alert>{error}</Alert> : <Spinner />

  return (
    <div className="mx-auto max-w-3xl">
      <PageTitle title={id ? 'Edit job' : 'Post a job'} />
      <form onSubmit={submit}>
        <Card className="space-y-4">
          <Alert>{error}</Alert>
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Job title">
              <input required maxLength={150} value={form.title} onChange={set('title')} className={inputClass} />
            </Field>
            <Field label="Company name">
              <input required maxLength={150} value={form.companyName} onChange={set('companyName')} className={inputClass} />
            </Field>
            <Field label="Location">
              <input required maxLength={100} value={form.location} onChange={set('location')} placeholder="e.g. Pune" className={inputClass} />
            </Field>
            <label className="flex items-center gap-2 self-end pb-2 text-sm text-slate-700">
              <input type="checkbox" checked={form.remote} onChange={(e) => setForm({ ...form, remote: e.target.checked })} />
              Remote work allowed
            </label>
            <Field label="Minimum salary (LPA)" hint="Lakhs per annum, e.g. 8">
              <input required type="number" min="0.1" step="0.1" value={form.minLpa} onChange={set('minLpa')} className={inputClass} />
            </Field>
            <Field label="Maximum salary (LPA)">
              <input required type="number" min="0.1" step="0.1" value={form.maxLpa} onChange={set('maxLpa')} className={inputClass} />
            </Field>
          </div>
          <Field label="Required skills">
            <SkillPicker value={form.skills} onChange={(skills) => setForm({ ...form, skills })} suggestions={allSkills} />
          </Field>
          <Field label="Description">
            <textarea required rows={8} maxLength={5000} value={form.description} onChange={set('description')} className={inputClass} />
          </Field>
          <div className="flex gap-3">
            <Button type="submit" disabled={busy}>
              {busy ? 'Saving…' : id ? 'Save changes' : 'Post job'}
            </Button>
            <Button type="button" variant="secondary" onClick={() => navigate('/recruiter/jobs')}>
              Cancel
            </Button>
          </div>
        </Card>
      </form>
    </div>
  )
}
