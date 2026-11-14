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
