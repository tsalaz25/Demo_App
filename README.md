cd gymapp
cat > README.md << 'EOF'
# GymApp

A mobile fitness tracking app for diet, workout, and weight progress.

## Tech Stack
- **Mobile:** Flutter (Dart) — iOS & Android
- **Backend:** Spring Boot 3.5 (Kotlin) + Gradle
- **Database:** PostgreSQL 16
- **Cache:** Redis
- **Auth:** JWT (Spring Security)

## Project Structure
gymapp Root
   |
   |---backend/   #Spring Boot REST API
   |
   |---mobile/   #Flutter Mobile App

---

## Dev Environment Setup (Mac arm64)

### Prerequisites
```bash
brew install postgresql@16 redis cocoapods
brew install --cask temurin@21 flutter
```

---

## Starting the Servers

### 1. PostgreSQL
```bash
brew services start postgresql@16
```

### 2. Redis
```bash
brew services start redis
```

### 3. Spring Boot Backend
```bash
cd gymapp/backend
./gradlew bootRun
```
Runs on: http://localhost:8080

### 4. Flutter Mobile App
```bash
cd gymapp/mobile
flutter run
```
Pick [1] for macOS desktop, or plug in iPhone for device.

---

## Stopping the Servers

```bash
# Stop backend — Ctrl+C in the terminal running bootRun

# Stop PostgreSQL and Redis
brew services stop postgresql@16
brew services stop redis
```

> PostgreSQL and Redis auto-start on login via brew services.
> To disable that behavior, stop them above and start manually each session.

---

## Database

- **Host:** localhost:5432  
- **Database:** gymapp_db  
- **User:** gymapp_user  

Connect via psql:
```bash
psql -U gymapp_user -d gymapp_db
```

---

## Environment Notes

- Java 21 (Temurin, arm64)
- Flutter 3.44.0 stable (darwin-arm64)
- Spring Boot 3.5.14
- PostgreSQL 16
- Redis (latest via brew)
