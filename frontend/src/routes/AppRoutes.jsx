import { Navigate, Route, Routes } from 'react-router-dom'
import { homePathFor, useAuth } from '../shared/AuthContext.jsx'
import Layout from '../shared/components/Layout.jsx'
import ProtectedRoute from '../shared/components/ProtectedRoute.jsx'
import LoginPage from '../features/auth/LoginPage.jsx'
import RegisterPage from '../features/auth/RegisterPage.jsx'
import JobsPage from '../features/seeker/pages/JobsPage.jsx'
import JobDetailPage from '../features/seeker/pages/JobDetailPage.jsx'
import SavedJobsPage from '../features/seeker/pages/SavedJobsPage.jsx'
import ApplicationsPage from '../features/seeker/pages/ApplicationsPage.jsx'
import AlertsPage from '../features/seeker/pages/AlertsPage.jsx'
import ProfilePage from '../features/seeker/pages/ProfilePage.jsx'
import MyJobsPage from '../features/recruiter/pages/MyJobsPage.jsx'
import JobFormPage from '../features/recruiter/pages/JobFormPage.jsx'
import ApplicantsPage from '../features/recruiter/pages/ApplicantsPage.jsx'

function Home() {
  const { user } = useAuth()
  return <Navigate to={homePathFor(user)} replace />
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route
        path="/seeker"
        element={
          <ProtectedRoute role="SEEKER">
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="jobs" replace />} />
        <Route path="jobs" element={<JobsPage />} />
        <Route path="jobs/:id" element={<JobDetailPage />} />
        <Route path="saved" element={<SavedJobsPage />} />
        <Route path="applications" element={<ApplicationsPage />} />
        <Route path="alerts" element={<AlertsPage />} />
        <Route path="profile" element={<ProfilePage />} />
      </Route>

      <Route
        path="/recruiter"
        element={
          <ProtectedRoute role="RECRUITER">
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="jobs" replace />} />
        <Route path="jobs" element={<MyJobsPage />} />
        <Route path="jobs/new" element={<JobFormPage />} />
        <Route path="jobs/:id/edit" element={<JobFormPage />} />
        <Route path="jobs/:id/applicants" element={<ApplicantsPage />} />
      </Route>

      <Route path="*" element={<Home />} />
    </Routes>
  )
}
