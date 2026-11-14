// Thin fetch wrapper. Vite proxies /api to the Spring Boot backend (see vite.config.js).
// Login uses a session cookie (JSESSIONID) that the browser sends automatically.
export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.status = status
  }
}

/**
 * api('/seeker/jobs?q=java')                      -> GET
 * api('/seeker/profile', { method: 'PUT', body }) -> JSON body
 * api('/seeker/profile/resume', { method: 'POST', form }) -> multipart (FormData)
 */
export async function api(path, { method = 'GET', body, form } = {}) {
  const options = { method, headers: {}, credentials: 'same-origin' }
  if (form) {
    options.body = form
  } else if (body !== undefined) {
    options.headers['Content-Type'] = 'application/json'
    options.body = JSON.stringify(body)
  }

  const res = await fetch(`/api${path}`, options)
  if (res.status === 204) return null

  let data = null
  try {
    data = await res.json()
  } catch {
    // no JSON body
  }
  if (!res.ok) {
    if (res.status === 401 && !path.startsWith('/auth/')) {
      window.dispatchEvent(new Event('auth:unauthorized')) // session expired
    }
    throw new ApiError(res.status, data?.message || `Something went wrong (${res.status})`)
  }
  return data
}
