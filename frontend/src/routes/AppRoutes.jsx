import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from '../features/auth/LoginPage.jsx'
import SeekerHome from '../features/seeker/pages/SeekerHome.jsx'
import RecruiterHome from '../features/recruiter/pages/RecruiterHome.jsx'

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/seeker" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/seeker/*" element={<SeekerHome />} />
      <Route path="/recruiter/*" element={<RecruiterHome />} />
    </Routes>
  )
}
