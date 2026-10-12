# TransitTrack

    transittrack/
      backend/    Spring Boot API   (http://localhost:8080)
      frontend/   web app           (http://localhost:5173)

No database setup needed - the backend uses an embedded database (H2)
that is created automatically in backend/data.

## 1. Start the backend (Terminal 1)
Needs Java 21.

    cd backend
    mvnw spring-boot:run          (Windows)
    ./mvnw spring-boot:run        (Mac/Linux, run `chmod +x mvnw` once first)

Wait for: "Started TransittrackApplication"

## 2. Start the frontend (Terminal 2)
Needs Node.js 18+. (No downloads needed - it uses a tiny built-in server.)

    cd frontend
    npm install
    npm run dev

The browser opens http://localhost:5173

## Admin login
    admin@transittrack.com / Admin@12345

## Deploy online (Render, free)
1. Push this whole folder to a GitHub repo.
2. render.com -> New -> Web Service -> pick the repo -> Runtime: Docker -> Free.
3. Add env var ADMIN_PASSWORD (your admin password) -> Create.
The root Dockerfile builds backend + frontend into one app automatically.
Free plan note: the embedded database resets when the service restarts.
