import { Navigate, useLocation } from 'react-router-dom'
import { homePathFor, useAuth } from '../AuthContext.jsx'
import { Spinner } from './ui.jsx'

/** Only lets in logged-in users with the right role; everyone else is redirected. */
export default function ProtectedRoute({ role, children }) {
  const { user, loading } = useAuth()
  const location = useLocation()
  if (loading) return <Spinner />
  if (!user) return <Navigate to="/login" state={{ from: location.pathname + location.search }} replace />
  if (role && user.role !== role) return <Navigate to={homePathFor(user)} replace />
  return children
}
