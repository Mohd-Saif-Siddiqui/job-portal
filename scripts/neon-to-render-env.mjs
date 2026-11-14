// Converts the Neon connection string in .env.local into the 3 variables Spring Boot / Render need.
// Writes them to .env.render.local (git-ignored). Prints NO secrets.
import fs from 'fs'

const env = Object.fromEntries(
  fs.readFileSync('.env.local', 'utf8')
    .split(/\r?\n/)
    .filter((l) => l.includes('=') && !l.startsWith('#'))
    .map((l) => { const i = l.indexOf('='); return [l.slice(0, i), l.slice(i + 1).replace(/^["']|["']$/g, '')] }),
)
// Use the direct (unpooled) connection: the app already has its own connection pool.
const raw = env.DATABASE_URL_UNPOOLED || env.DATABASE_URL
if (!raw) throw new Error('No DATABASE_URL found in .env.local')
const u = new URL(raw)
const jdbc = `jdbc:postgresql://${u.host}${u.pathname}?sslmode=require`
const out = [
  `SPRING_DATASOURCE_URL=${jdbc}`,
  `SPRING_DATASOURCE_USERNAME=${decodeURIComponent(u.username)}`,
  `SPRING_DATASOURCE_PASSWORD=${decodeURIComponent(u.password)}`,
].join('\n') + '\n'
fs.writeFileSync('.env.render.local', out)
console.log('Wrote .env.render.local with: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD')
console.log('Host:', u.host.replace(/^[^.]+/, '<endpoint>'), '| database:', u.pathname.slice(1), '| user:', decodeURIComponent(u.username))
