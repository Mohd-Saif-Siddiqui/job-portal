import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { api } from '../api/client.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // On first load, ask the server who is logged in (the session cookie tells it).
  useEffect(() => {
    api('/auth/me')
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false))
    const onExpired = () => setUser(null)
    window.addEventListener('auth:unauthorized', onExpired)
    return () => window.removeEventListener('auth:unauthorized', onExpired)
  }, [])

  const login = useCallback(async (email, password) => {
    const u = await api('/auth/login', { method: 'POST', body: { email, password } })
    setUser(u)
    return u
  }, [])

  const register = useCallback(async (fields) => {
    const u = await api('/auth/register', { method: 'POST', body: fields })
    setUser(u)
    return u
  }, [])

  const logout = useCallback(async () => {
    await api('/auth/logout', { method: 'POST' }).catch(() => {})
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)

export const homePathFor = (user) =>
  !user ? '/login' : user.role === 'RECRUITER' ? '/recruiter/jobs' : '/seeker/jobs'
