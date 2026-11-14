# Book Review API

A REST API for browsing books and posting reviews. Users log in and receive a
JWT, and admins manage the book catalog. I built it to learn Spring Security
and stateless authentication properly, including automated tests for the
access rules.

## Tech stack

- Java 25, Spring Boot 4.1
- Spring Security with JWT (JJWT) for stateless authentication
- Spring Data JPA, PostgreSQL
- JUnit 5, MockMvc, Mockito, H2 (in-memory, tests only)
- Maven

## Running locally

**Requirements:** JDK 25 and a running PostgreSQL instance with an empty
database named `bookdatabase`.

**Environment variables:**

| Variable | Purpose |
| --- | --- |
| `JWT_SECRET` | Base64 signing key, at least 32 bytes. Generate one with `openssl rand -base64 32` |
| `DB_PASSWORD` | Password of your PostgreSQL user |

PowerShell:

```powershell
$env:JWT_SECRET = "<your key>"
$env:DB_PASSWORD = "<your password>"
.\mvnw.cmd spring-boot:run
```

Linux / macOS:

```bash
export JWT_SECRET="<your key>"
export DB_PASSWORD="<your password>"
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. The signing key is never stored in
the repository, and the app refuses to start without `JWT_SECRET`.

## Authentication

Log in to get a token:

```
POST /login
Content-Type: application/json

{"username": "alice", "password": "your-password"}
```

The response body is the JWT. Send it on protected requests:

```
GET /reviews/my-reviews
Authorization: Bearer <token>
```

Tokens expire after one hour by default (`jwt.expiration-ms`). Passwords are
hashed with BCrypt. A missing, invalid, or expired token returns `401`; a valid
token without the required role returns `403`.

## Access rules

| Endpoint | Access |
| --- | --- |
| `POST /users/register`, `POST /login` | Public |
| `GET /books/**`, `GET /reviews/**` | Public |
| `GET /reviews/my-reviews` | Logged-in user |
| `POST`, `PUT`, `DELETE /books/**` | Admin only |
| `GET /users` | Admin only |
| Everything else | Logged-in user |

## Tests

```bash
./mvnw test
```

The tests use an in-memory H2 database and their own throwaway signing key, so
they need no setup. They cover the JWT service and the security rules: public
endpoints, missing, tampered and expired tokens, and role restrictions.

## Status

In progress. Planned: refresh tokens, logout, Docker, CI.