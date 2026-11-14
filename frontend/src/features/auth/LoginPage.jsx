import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { homePathFor, useAuth } from '../../shared/AuthContext.jsx'
import { Alert, Button, Card, Field, inputClass } from '../../shared/components/ui.jsx'

export default function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) return <Navigate to={homePathFor(user)} replace />

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const u = await login(email, password)
      navigate(location.state?.from || homePathFor(u), { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4">
      <Card className="w-full max-w-sm">
        <h1 className="text-center text-2xl font-bold text-indigo-600">JobPortal</h1>
        <p className="mb-5 mt-1 text-center text-sm text-slate-500">Log in to find or post jobs</p>
        <form onSubmit={onSubmit} className="space-y-4">
          <Alert>{error}</Alert>
          <Field label="Email">
            <input type="email" required autoComplete="username" value={email} onChange={(e) => setEmail(e.target.value)} className={inputClass} />
          </Field>
          <Field label="Password">
            <input type="password" required autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} className={inputClass} />
          </Field>
          <Button type="submit" disabled={busy} className="w-full">
            {busy ? 'Logging in…' : 'Log in'}
          </Button>
        </form>
        <p className="mt-4 text-center text-sm text-slate-600">
          New here?{' '}
          <Link to="/register" className="font-medium text-indigo-600 hover:underline">
            Create an account
          </Link>
        </p>
        <div className="mt-5 rounded-lg bg-slate-50 p-3 text-xs text-slate-500">
          <p className="font-medium text-slate-600">Demo accounts</p>
          <p>Seeker: seeker@demo.com / Seeker@123</p>
          <p>Recruiter: recruiter@demo.com / Recruiter@123</p>
        </div>
      </Card>
    </div>
  )
}
