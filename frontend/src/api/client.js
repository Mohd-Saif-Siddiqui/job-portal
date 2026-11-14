// Thin fetch wrapper. Vite proxies /api to the Spring Boot backend (see vite.config.js).
export async function api(path, options = {}) {
  const token = localStorage.getItem('token')
  const res = await fetch(`/api${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...options,
  })
  if (!res.ok) throw new Error(`Request failed: ${res.status}`)
  return res.json()
}
