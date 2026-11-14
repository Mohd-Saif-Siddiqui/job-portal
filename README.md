# Job Portal

End-to-end job portal with two kinds of users:
- **Job seekers** build a profile (skills, resume), search jobs by skill / salary / location / remote, save jobs, apply, track applications and set up job alerts.
- **Recruiters** post jobs and manage applicants on a board (Applied → Shortlisted → Interview → Offer / Rejected).

## Tech
- `frontend/`  React + Vite + Tailwind CSS v4 (features: auth, seeker, recruiter)
- `backend/`   Spring Boot 3.3 (Java 17), Spring Data JPA, Spring Security (session login + BCrypt)
- `infra/`     Docker Compose for PostgreSQL 16

## Run locally (3 terminals)
1. Database: `cd infra && docker compose up -d`   (Postgres on host port **5433**)
2. Backend:  `cd backend && mvn spring-boot:run`  (http://localhost:8080/api/seeker/ping)
3. Frontend: `cd frontend && npm install && npm run dev`  (http://localhost:5173)

On the first start with an empty database the backend inserts demo data (33 jobs, ~30 skills) and two demo logins:

| Role | Email | Password |
|---|---|---|
| Seeker | seeker@demo.com | Seeker@123 |
| Recruiter | recruiter@demo.com | Recruiter@123 |

Turn the demo data off with `app.seed-demo-data: false` in `backend/src/main/resources/application.yml`.

## How it works
- **Login:** email + password. The server keeps the login in an HTTP session (cookie `JSESSIONID`); passwords are stored as BCrypt hashes. Roles: `SEEKER`, `RECRUITER`.
- **Salaries** are stored in rupees per year and shown as LPA (lakhs per annum).
- **Resumes** are saved in `backend/uploads/` (PDF/DOC/DOCX, max 5 MB).
- **Database tables** are created automatically by Hibernate (`ddl-auto: update`): users, seeker_profiles, skills, seeker_skills, jobs, job_skills, applications, saved_jobs, job_alerts.

## API overview
| Area | Endpoints |
|---|---|
| Auth | `POST /api/auth/register`, `/login`, `/logout`, `GET /api/auth/me` |
| Skills | `GET /api/skills` |
| Seeker | `/api/seeker/profile` (+ `/resume`), `/api/seeker/jobs` (search, `/saved`, `/{id}`, `/{id}/save`), `/api/seeker/applications`, `/api/seeker/alerts` |
| Recruiter | `/api/recruiter/jobs` (+ `/{id}`, `/{id}/status`, `/{id}/applicants`), `/api/recruiter/applications/{id}/status`, `/api/recruiter/applications/{id}/resume` |

## Not built yet
Email notifications, admin moderation, analytics, company pages, AI resume-to-job matching, database migrations (Flyway), automated backend tests.

## Deploy for free (Render + Neon)
The `Dockerfile` builds the React app and the Spring Boot backend into **one** image, and Spring Boot serves both. One service means the login cookie just works (same domain, no CORS).

1. **Database (Neon):** create a free project at neon.tech. From its connection details note the host, database name, user and password.
2. **Push the code to GitHub** (Render deploys from a repo).
3. **App (Render):** New → Blueprint → pick the repo (it reads `render.yaml`). Fill in the three environment variables:
   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<neon-host>/<database>?sslmode=require`  (JDBC format, **without** user:password in it)
   - `SPRING_DATASOURCE_USERNAME` = your Neon user
   - `SPRING_DATASOURCE_PASSWORD` = your Neon password
4. Deploy. The first build takes a few minutes. Open the `*.onrender.com` URL and log in with the demo accounts (they are created on the first start).

Free-tier notes: the service sleeps after 15 minutes without traffic (the next visit takes about a minute to wake it), and uploaded resumes are lost when the service restarts or redeploys. Set `SEED_DEMO_DATA=false` to skip demo data.

Test the same image locally: `docker build -t jobportal .` then run it with the three `SPRING_DATASOURCE_*` variables pointing at a Postgres database.
