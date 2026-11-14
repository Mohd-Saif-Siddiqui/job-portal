import { STATUS_LABEL } from '../format.js'

export const inputClass =
  'w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder:text-slate-400 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200'

export function Field({ label, hint, children }) {
  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium text-slate-700">{label}</span>
      {children}
      {hint && <span className="mt-1 block text-xs text-slate-500">{hint}</span>}
    </label>
  )
}

const variants = {
  primary: 'bg-indigo-600 text-white hover:bg-indigo-700 disabled:bg-indigo-300',
  secondary: 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50 disabled:opacity-50',
  danger: 'border border-red-200 bg-white text-red-600 hover:bg-red-50 disabled:opacity-50',
}

export function Button({ variant = 'primary', className = '', ...props }) {
  return (
    <button
      {...props}
      className={`inline-flex items-center justify-center gap-1 rounded-lg px-4 py-2 text-sm font-medium transition-colors disabled:cursor-not-allowed ${variants[variant]} ${className}`}
    />
  )
}

export function Alert({ kind = 'error', children }) {
  if (!children) return null
  const styles =
    kind === 'error'
      ? 'border-red-200 bg-red-50 text-red-700'
      : 'border-green-200 bg-green-50 text-green-700'
  return (
    <div role={kind === 'error' ? 'alert' : 'status'} className={`rounded-lg border px-3 py-2 text-sm ${styles}`}>
      {children}
    </div>
  )
}

export function Spinner({ label = 'Loading…' }) {
  return <p className="py-10 text-center text-sm text-slate-500">{label}</p>
}

export function Card({ className = '', ...props }) {
  return <div {...props} className={`rounded-xl border border-slate-200 bg-white p-5 shadow-sm ${className}`} />
}

export function SkillTag({ children }) {
  return (
    <span className="rounded-full bg-indigo-50 px-2.5 py-0.5 text-xs font-medium text-indigo-700">{children}</span>
  )
}

const statusStyles = {
  APPLIED: 'bg-blue-50 text-blue-700',
  SHORTLISTED: 'bg-amber-50 text-amber-700',
  INTERVIEW: 'bg-purple-50 text-purple-700',
  OFFER: 'bg-green-50 text-green-700',
  REJECTED: 'bg-red-50 text-red-700',
  OPEN: 'bg-green-50 text-green-700',
  CLOSED: 'bg-slate-100 text-slate-600',
}

export function StatusBadge({ status }) {
  return (
    <span className={`rounded-full px-2.5 py-0.5 text-xs font-semibold ${statusStyles[status] || ''}`}>
      {STATUS_LABEL[status] || status.charAt(0) + status.slice(1).toLowerCase()}
    </span>
  )
}

export function PageTitle({ title, children }) {
  return (
    <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
      <h1 className="text-2xl font-bold text-slate-900">{title}</h1>
      {children}
    </div>
  )
}

export function Empty({ children }) {
  return (
    <div className="rounded-xl border border-dashed border-slate-300 bg-white py-12 text-center text-sm text-slate-500">
      {children}
    </div>
  )
}
