import { useEffect, useRef, useState } from 'react'
import { api } from '../../../api/client.js'
import { lpaToRupees, toLpa } from '../../../shared/format.js'
import useSkills from '../../../shared/hooks/useSkills.js'
import { Alert, Button, Card, Field, PageTitle, Spinner, inputClass } from '../../../shared/components/ui.jsx'

export default function ProfilePage() {
  const allSkills = useSkills()
  const [form, setForm] = useState(null)
  const [resumeName, setResumeName] = useState(null)
  const [newSkill, setNewSkill] = useState('')
  const [newYears, setNewYears] = useState(1)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [busy, setBusy] = useState(false)
  const fileRef = useRef(null)

  const load = (p) => {
    setForm({
      fullName: p.fullName,
      email: p.email,
      headline: p.headline || '',
      location: p.location || '',
      bio: p.bio || '',
      expectedLpa: p.expectedSalary ? toLpa(p.expectedSalary) : '',
      skills: p.skills,
    })
    setResumeName(p.resumeName)
  }

  useEffect(() => {
    api('/seeker/profile')
      .then(load)
      .catch((e) => setError(e.message))
  }, [])

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const addSkill = () => {
    const name = newSkill.trim()
    if (!name) return
    if (!form.skills.some((s) => s.name.toLowerCase() === name.toLowerCase())) {
      const known = allSkills.find((s) => s.toLowerCase() === name.toLowerCase())
      setForm({ ...form, skills: [...form.skills, { name: known || name, yearsExperience: Number(newYears) || 0 }] })
    }
    setNewSkill('')
  }

  const save = async (e) => {
    e.preventDefault()
    setError('')
    setNotice('')
    setBusy(true)
    try {
      const p = await api('/seeker/profile', {
        method: 'PUT',
        body: {
          fullName: form.fullName,
          headline: form.headline,
          location: form.location,
          bio: form.bio,
          expectedSalary: form.expectedLpa === '' ? null : lpaToRupees(form.expectedLpa),
          skills: form.skills,
        },
      })
      load(p)
      setNotice('Profile saved.')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const upload = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    setError('')
    setNotice('')
    const fd = new FormData()
    fd.append('file', file)
    try {
      const p = await api('/seeker/profile/resume', { method: 'POST', form: fd })
      setResumeName(p.resumeName)
      setNotice('Resume uploaded.')
    } catch (err) {
      setError(err.message)
    } finally {
      if (fileRef.current) fileRef.current.value = ''
    }
  }

  const removeResume = async () => {
    try {
      await api('/seeker/profile/resume', { method: 'DELETE' })
      setResumeName(null)
      setNotice('Resume removed.')
    } catch (err) {
      setError(err.message)
    }
  }

  if (!form) return error ? <Alert>{error}</Alert> : <Spinner />

  return (
    <div className="mx-auto max-w-3xl">
      <PageTitle title="My profile" />
      <div className="mb-4 space-y-2">
        <Alert>{error}</Alert>
        <Alert kind="success">{notice}</Alert>
      </div>

      <form onSubmit={save} className="space-y-5">
        <Card className="space-y-4">
          <h2 className="font-semibold text-slate-900">About you</h2>
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Full name">
              <input required maxLength={100} value={form.fullName} onChange={set('fullName')} className={inputClass} />
            </Field>
            <Field label="Email">
              <input value={form.email} disabled className={`${inputClass} bg-slate-100`} />
            </Field>
            <Field label="Headline" hint="e.g. Java Developer | 2 years experience">
              <input maxLength={150} value={form.headline} onChange={set('headline')} className={inputClass} />
            </Field>
            <Field label="Location">
              <input maxLength={100} value={form.location} onChange={set('location')} className={inputClass} />
            </Field>
            <Field label="Expected salary (LPA)" hint="Lakhs per annum, e.g. 8 or 12.5">
              <input type="number" min="0" step="0.1" value={form.expectedLpa} onChange={set('expectedLpa')} className={inputClass} />
            </Field>
          </div>
          <Field label="Summary">
            <textarea rows={4} maxLength={2000} value={form.bio} onChange={set('bio')} className={inputClass} />
          </Field>
        </Card>

        <Card className="space-y-3">
          <h2 className="font-semibold text-slate-900">Skills</h2>
          {form.skills.length === 0 && <p className="text-sm text-slate-500">No skills added yet.</p>}
          <ul className="space-y-2">
            {form.skills.map((s) => (
              <li key={s.name} className="flex items-center gap-3 rounded-lg border border-slate-200 px-3 py-2 text-sm">
                <span className="flex-1 font-medium text-slate-800">{s.name}</span>
                <span className="text-slate-500">
                  {s.yearsExperience} yr{s.yearsExperience === 1 ? '' : 's'}
                </span>
                <button
                  type="button"
                  aria-label={`Remove ${s.name}`}
                  onClick={() => setForm({ ...form, skills: form.skills.filter((x) => x.name !== s.name) })}
                  className="text-slate-400 hover:text-red-600"
                >
                  ✕
                </button>
              </li>
            ))}
          </ul>
          <div className="flex flex-wrap gap-2">
            <input
              list="profile-skill-list"
              value={newSkill}
              onChange={(e) => setNewSkill(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  e.preventDefault()
                  addSkill()
                }
              }}
              placeholder="Skill, e.g. Java"
              aria-label="Skill name"
              className={`${inputClass} flex-1`}
            />
            <datalist id="profile-skill-list">
              {allSkills.map((s) => (
                <option key={s} value={s} />
              ))}
            </datalist>
            <input type="number" min="0" max="50" value={newYears} onChange={(e) => setNewYears(e.target.value)} aria-label="Years of experience" className={`${inputClass} w-24`} />
            <Button type="button" variant="secondary" onClick={addSkill}>
              Add skill
            </Button>
          </div>
        </Card>

        <div>
          <Button type="submit" disabled={busy}>
            {busy ? 'Saving…' : 'Save profile'}
          </Button>
        </div>
      </form>

      <Card className="mt-5 space-y-3">
        <h2 className="font-semibold text-slate-900">Resume</h2>
        {resumeName ? (
          <div className="flex flex-wrap items-center gap-3 text-sm">
            <span>📄 {resumeName}</span>
            <a href="/api/seeker/profile/resume" className="text-indigo-600 hover:underline">
              Download
            </a>
            <button type="button" onClick={removeResume} className="text-red-600 hover:underline">
              Remove
            </button>
          </div>
        ) : (
          <p className="text-sm text-slate-500">No resume uploaded yet.</p>
        )}
        <div>
          <label className="inline-block cursor-pointer rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50">
            {resumeName ? 'Replace resume' : 'Upload resume'}
            <input ref={fileRef} type="file" accept=".pdf,.doc,.docx" onChange={upload} className="sr-only" data-testid="resume-input" />
          </label>
          <span className="ml-3 text-xs text-slate-500">PDF, DOC or DOCX, up to 5 MB</span>
        </div>
      </Card>
    </div>
  )
}
