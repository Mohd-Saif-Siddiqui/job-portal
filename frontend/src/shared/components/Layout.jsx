import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'

const seekerLinks = [
  ['/seeker/jobs', 'Find jobs'],
  ['/seeker/saved', 'Saved'],
  ['/seeker/applications', 'My applications'],
  ['/seeker/alerts', 'Alerts'],
  ['/seeker/profile', 'Profile'],
]
const recruiterLinks = [
  ['/recruiter/jobs', 'My jobs'],
  ['/recruiter/jobs/new', 'Post a job'],
]

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const links = user.role === 'RECRUITER' ? recruiterLinks : seekerLinks

  const onLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center gap-x-6 gap-y-2 px-4 py-3">
          <span className="text-lg font-bold text-indigo-600">JobPortal</span>
          <nav className="flex flex-1 flex-wrap gap-1">
            {links.map(([to, label]) => (
              <NavLink
                key={to}
                to={to}
                end={to === '/recruiter/jobs'}
                className={({ isActive }) =>
                  `rounded-lg px-3 py-1.5 text-sm font-medium ${
                    isActive ? 'bg-indigo-50 text-indigo-700' : 'text-slate-600 hover:bg-slate-100'
                  }`
                }
              >
                {label}
              </NavLink>
            ))}
          </nav>
          <div className="flex items-center gap-3 text-sm">
            <span className="text-slate-600" data-testid="user-name">
              {user.fullName} <span className="text-xs text-slate-400">({user.role.toLowerCase()})</span>
            </span>
            <button onClick={onLogout} className="rounded-lg border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-50">
              Log out
            </button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-6xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  )
}
