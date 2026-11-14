# Job Portal

End-to-end job portal for job seekers and recruiters. Starting with the seeker side.

## Structure
- `frontend/` React + Vite + Tailwind (features/seeker, features/recruiter, features/auth)
- `backend/`  Spring Boot 3 (Java 17), packages: auth, seeker, recruiter, common
- `infra/`    Docker Compose (Postgres) and env files

## Run locally
1. Database:  `cd infra && docker compose up -d`
2. Backend:   `cd backend && mvn spring-boot:run`   (check http://localhost:8080/api/seeker/ping)
3. Frontend:  `cd frontend && npm install && npm run dev`   (http://localhost:5173)
