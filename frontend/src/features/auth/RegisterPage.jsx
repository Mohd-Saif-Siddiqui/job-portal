import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { homePathFor, useAuth } from '../../shared/AuthContext.jsx'
import { Alert, Button, Card, Field, inputClass } from '../../shared/components/ui.jsx'

export default function RegisterPage() {
  const { user, register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', password: '', role: 'SEEKER' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) return <Navigate to={homePathFor(user)} replace />

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const u = await register(form)
      navigate(u.role === 'SEEKER' ? '/seeker/profile' : homePathFor(u), { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-8">
      <Card className="w-full max-w-sm">
        <h1 className="text-center text-2xl font-bold text-indigo-600">Create your account</h1>
        <form onSubmit={onSubmit} className="mt-5 space-y-4">
          <Alert>{error}</Alert>
          <div className="grid grid-cols-2 gap-2" role="radiogroup" aria-label="I am a">
            {[
              ['SEEKER', 'Job seeker'],
              ['RECRUITER', 'Recruiter'],
            ].map(([value, label]) => (
              <button
                type="button"
                key={value}
                role="radio"
                aria-checked={form.role === value}
                onClick={() => setForm({ ...form, role: value })}
                className={`rounded-lg border px-3 py-2 text-sm font-medium ${
                  form.role === value ? 'border-indigo-600 bg-indigo-50 text-indigo-700' : 'border-slate-300 text-slate-600'
                }`}
              >
                {label}
              </button>
            ))}
          </div>
          <Field label="Full name">
            <input required maxLength={100} value={form.fullName} onChange={set('fullName')} className={inputClass} />
          </Field>
          <Field label="Email">
            <input type="email" required autoComplete="username" value={form.email} onChange={set('email')} className={inputClass} />
          </Field>
          <Field label="Password" hint="At least 8 characters">
            <input type="password" required minLength={8} maxLength={72} autoComplete="new-password" value={form.password} onChange={set('password')} className={inputClass} />
          </Field>
          <Button type="submit" disabled={busy} className="w-full">
            {busy ? 'Creating…' : 'Create account'}
          </Button>
        </form>
        <p className="mt-4 text-center text-sm text-slate-600">
          Already have an account?{' '}
          <Link to="/login" className="font-medium text-indigo-600 hover:underline">
            Log in
          </Link>
        </p>
      </Card>
    </div>
  )
}
