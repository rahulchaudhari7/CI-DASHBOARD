# Pulse CI — Smart CI Pipeline Dashboard

A runnable academic CI demonstration with a React/Vite dashboard, Spring Boot REST API, automated tests, Pact provider verification, and a GitHub Actions workflow. Demo records are labeled. The Run Pipeline endpoint creates a controlled **SIMULATED** record and never claims to run a remote build.

## Requirements

- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Node.js 20.19+ (or 22.12+) and npm (`node --version`, `npm.cmd --version` in PowerShell)
- Docker Desktop only for optional Testcontainers checks or Docker Compose
- Git and a GitHub repository to observe Actions workflow runs

## Run locally (Windows PowerShell)

1. Backend tests: `cd backend; mvn test`
2. Start backend: `cd backend; mvn spring-boot:run` (API at `http://localhost:8080`; health at `/actuator/health`).
3. In a second terminal, frontend: `cd frontend; Copy-Item .env.example .env; npm.cmd install; npm.cmd run dev` (dashboard at `http://localhost:5173`).
4. Optional Docker Compose: from the repository root run `docker compose up --build`.

## Tests and reports

`mvn test` runs JUnit/Mockito service unit tests and Spring Boot + MockMvc API tests. The Pact consumer test generates a local contract; provider verification reads the checked-in contract from `backend/pacts`. No Pact Broker or credentials are configured. The PostgreSQL Testcontainers smoke test runs when Docker is available and is skipped when Docker is absent. Reports are generated under `backend/target/surefire-reports` and uploaded by Actions.

## API

- `GET /api/dashboard/summary` — aggregate metrics with explicit `dataMode: DEMO`
- `GET /api/pipelines` and `GET /api/pipelines/{id}`
- `POST /api/pipelines/run` — JSON `{ "branch": "main", "triggeredBy": "Student" }`; responds HTTP 202 with a `SIMULATED` run
- `GET /api/tests/results`, `/api/reports`, `/api/services`
- `GET /actuator/health`

## GitHub Actions

`.github/workflows/ci.yml` runs on pushes and pull requests targeting `main`, sets up Temurin 17 with Maven caching, runs `mvn verify`, then uploads test and Pact artifacts. `ubuntu-latest` provides Docker for any enabled Testcontainers tests. This dashboard does not query GitHub or claim live workflow status. Push the repository to GitHub and open Actions to see actual workflow runs.

## Experiment 2 — Continuous Deployment and Canary Release

**Aim:** Design and implement a Continuous Deployment pipeline with containerization and an automated release strategy.

**Objectives:** Understand CD; package the backend as a Docker image; automate verification and image publication with GitHub Actions; route a small share of requests to a canary, check health, promote it, or roll back.

**CO mapping:** CO6 — BT6. **Prerequisites:** The CI experiment above, basic Docker knowledge, and deployment concepts.

### Local Docker canary lab

Install and start Docker Desktop, then from the repository root in PowerShell:

```powershell
.\ops\deploy-canary.ps1
.\ops\check-canary.ps1
```

The Compose stack runs stable and canary backend containers behind Nginx. The gateway starts at a 90:10 stable-to-canary traffic split at `http://localhost:8180`. Each request to `/api/deployment/instance` identifies the serving slot and version. Promote with `.\ops\promote-canary.ps1` or roll back with `.\ops\rollback-canary.ps1`. Nginx requires non-zero upstream weights, so promotion and rollback use a 1000:1 split (over 99.9% to the selected slot). The PowerShell scripts check container health and verify the selected route. This is a local strategy demonstration; use real monitoring, error-rate thresholds, and a review window before production promotion.

### Automated CD on GitHub

`.github/workflows/cd.yml` runs the backend and Pact verification on pushes to `main` and manual dispatches. On success, it builds and publishes the tested backend image to GHCR tagged with the commit SHA. Configure repository variable `CD_DEPLOY_ENABLED=true` and create a GitHub Environment named `production` with these secrets to enable the SSH deployment job:

- `CD_HOST` — deployment server hostname
- `CD_USER` — SSH user with Docker Compose access
- `CD_SSH_KEY` — private key for that user
- `CD_KNOWN_HOSTS` — verified SSH host-key line(s)

The host must have Docker Engine and the Compose plugin. The workflow copies the Compose and Nginx config, pulls the new image, starts a 90:10 canary, checks stable/canary/gateway health, and shifts traffic to the candidate only when checks pass. On failure it restores stable traffic and fails the workflow. Without the variable and secrets, image publication still runs and deployment is skipped; no live server is claimed. Configure required reviewers on the `production` environment if your release policy calls for approval.

**Expected outputs:** SHA-tagged container image, deployment health checks, observed stable/canary request routing, and either promotion or rollback. **Learning outcomes:** Docker image delivery, deployment automation, weighted routing, canary verification, and rollback behavior.

## Academic practical

**Aim:** Design a CI pipeline that automates building and testing applications.

**Objectives:** Understand CI; implement unit, integration, and contract testing; automate tests using GitHub Actions; visualize build and test states.

**Software requirements:** Java 17, Spring Boot 3, Maven, React, TypeScript, Node.js, Git/GitHub; Docker is optional for Testcontainers.

**Architecture:** A responsive React client calls a Spring Boot REST API. The initial pipeline repository is in-memory and seeded with clearly marked demo data. A controlled run adds a simulated record. GitHub Actions independently executes Maven verification and stores test artifacts.

**Implementation methodology:** Build DTO-based API endpoints and service logic; cover service behavior with JUnit; cover HTTP behavior with MockMvc; verify provider behavior against local Pact contracts; automate Maven verification on pushes and pull requests.

**Testing methodology:** Unit tests isolate a small class or behavior. Integration tests start the Spring application and exercise endpoints through MockMvc. Contract tests compare provider responses to consumer expectations. Testcontainers can validate container-backed dependencies when one is added and Docker is available.

**CI concepts:** Continuous Integration means integrating small changes frequently and automatically building and testing each change. Unit tests isolate a small class or behavior. Integration tests check collaborating application components and HTTP boundaries. Contract tests check that a provider continues to meet a consumer's request/response expectations. The three test types find different regressions and complement each other.

**Expected outputs:** Build status, test suite summaries, execution history, Maven reports, and workflow artifacts. Demo figures are illustrative until connected to real generated results.

**Learning outcomes:** Configure an automated Java build; write unit and API tests; understand consumer/provider contracts; interpret build reports; connect a frontend to an API; explain the CI feedback loop.

**Conclusion:** This project demonstrates the main CI workflow and testing layers while distinguishing demo pipeline activity from real Maven and GitHub Actions results.
