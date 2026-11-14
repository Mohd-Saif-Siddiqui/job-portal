// Salaries are stored in rupees per year; Indian job sites show them in LPA (lakhs per annum).
const LAKH = 100000

export const toLpa = (rupees) => {
  const v = rupees / LAKH
  return Number.isInteger(v) ? String(v) : v.toFixed(1)
}

export const lpaToRupees = (lpa) => Math.round(Number(lpa) * LAKH)

export const salaryRange = (min, max) => `₹${toLpa(min)} – ${toLpa(max)} LPA`

export function timeAgo(iso) {
  const mins = Math.floor((Date.now() - new Date(iso).getTime()) / 60000)
  if (mins < 1) return 'just now'
  if (mins < 60) return `${mins} min ago`
  const hrs = Math.floor(mins / 60)
  if (hrs < 24) return `${hrs} hour${hrs > 1 ? 's' : ''} ago`
  const days = Math.floor(hrs / 24)
  if (days < 30) return `${days} day${days > 1 ? 's' : ''} ago`
  return new Date(iso).toLocaleDateString('en-IN')
}

// Application stages, in order. REJECTED is a dead end and shown separately.
export const STAGES = ['APPLIED', 'SHORTLISTED', 'INTERVIEW', 'OFFER']
export const ALL_STATUSES = [...STAGES, 'REJECTED']
export const STATUS_LABEL = {
  APPLIED: 'Applied',
  SHORTLISTED: 'Shortlisted',
  INTERVIEW: 'Interview',
  OFFER: 'Offer',
  REJECTED: 'Rejected',
}
