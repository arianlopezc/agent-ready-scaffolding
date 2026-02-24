# Agent-Ready Scaffolding

A companion repository for the article [Your AI Agent Doesn't Need More Guardrails. It Needs a Better Project](https://medium.com/@arian.lopezc/your-ai-agent-doesnt-need-more-guardrails-it-needs-a-better-project-896be18e260c).

This is a Java multi-module Maven project that demonstrates **five layers of scaffolding** that reduce AI code review to near-zero. The application code is intentionally minimal — the value is in the scaffolding files that guide AI agents to produce consistent, high-quality code.

## The Five Layers

| # | Layer | What It Does | Files |
|---|-------|-------------|-------|
| 1 | Architecture Map | Module boundaries, dependency rules, code patterns | `CLAUDE.md`, `AGENTS.md`, `.cursor/rules/java.mdc`, `.windsurf/rules/project.md`, `.github/copilot-instructions.md`, `.github/instructions/java.instructions.md` |
| 2 | Code Conventions | Formatting, naming, idioms with correct/wrong examples | `.ai/prompts/JAVA_CODE_QUALITY.md` |
| 3 | Task Playbooks | Step-by-step guides for common tasks | `.ai/prompts/add-entity.md`, `.ai/prompts/add-endpoint.md` |
| 4 | Automated Enforcement | PostToolUse hooks, ArchUnit tests, Spotless, Maven Enforcer, CI pipeline | `.claude/settings.json`, `API/.../ArchitectureTest.java`, `pom.xml` (plugins), `.github/workflows/ci.yml` |
| 5 | Testing Infrastructure | TDD workflow, test templates, troubleshooting table | `.ai/prompts/testing-guide.md` |

## Quick Start

```bash
# Clone the repo
git clone https://github.com/arianlopezc/agent-ready-scaffolding.git
cd agent-ready-scaffolding

# Start PostgreSQL
docker-compose up -d

# Compile all modules
mvn clean compile -B

# Run all tests (requires Docker for Testcontainers)
mvn test -B

# Check formatting
mvn spotless:check -B
```

## Project Structure

```
agent-ready-scaffolding/
├── Model/               Domain entities, DTOs, Enums (Immutables)
├── SQLDatastore/        Spring Data JDBC repositories + Flyway migrations
├── Shared/              Business services with circuit breaker
├── API/                 REST controllers, config, health probes
├── docker-compose.yml   PostgreSQL for local development
├── pom.xml              Parent POM (Spotless, Maven Enforcer, JaCoCo)
│
├── CLAUDE.md                                Layer 1 — Claude Code context
├── AGENTS.md                                Layer 1 — Cross-agent context
├── .cursor/rules/java.mdc                   Layer 1 — Cursor rules
├── .windsurf/rules/project.md               Layer 1 — Windsurf rules
├── .github/copilot-instructions.md          Layer 1 — Copilot repo-wide context
├── .github/instructions/java.instructions.md Layer 1 — Copilot file-specific rules
│
├── .ai/prompts/JAVA_CODE_QUALITY.md         Layer 2 — Code conventions spec
├── .ai/prompts/add-entity.md                Layer 3 — Entity playbook
├── .ai/prompts/add-endpoint.md              Layer 3 — Endpoint playbook
│
├── .claude/settings.json                    Layer 4 — Hooks + permissions
├── API/.../ArchitectureTest.java            Layer 4 — Boundary enforcement
├── .github/workflows/ci.yml                 Layer 4 — CI pipeline
├── .github/workflows/copilot-setup-steps.yml Layer 4 — Copilot cloud setup
│
└── .ai/prompts/testing-guide.md             Layer 5 — Testing infrastructure
```

Dependencies flow inward: `API` → `Shared` → `SQLDatastore` → `Model`

## Supported AI Agents

This project includes context files for **five** AI coding agents:

| Agent | Context Files |
|-------|--------------|
| **Claude Code** | `CLAUDE.md`, `.claude/settings.json` (hooks), `.claude/skills/` |
| **Cursor** | `.cursor/rules/java.mdc` |
| **Windsurf** | `.windsurf/rules/project.md` |
| **GitHub Copilot** | `.github/copilot-instructions.md`, `.github/instructions/java.instructions.md`, `.github/workflows/copilot-setup-steps.yml` |
| **All agents** | `AGENTS.md`, `.ai/prompts/` (shared guides) |

## Try It

Open this project in your AI coding agent of choice and try:

- *"Add a new entity called Project with name and description fields"*
- *"Add a PATCH endpoint to update a placeholder's name"*
- *"Write a test for the delete endpoint"*

The agent will read the architecture map, follow the conventions spec, use the task playbook, and the enforcement layer (hooks, ArchUnit, CI) will catch anything it misses.

## Technology Stack

- Java 21 / Spring Boot 3.4
- Spring Data JDBC / PostgreSQL 17 / Flyway
- Testcontainers / ArchUnit / JaCoCo
- Spotless (Google Java Format) / Maven Enforcer
- Resilience4j (Circuit Breaker)
- SpringDoc OpenAPI (Swagger UI)

## What This Project Does NOT Include

This is a scaffolding demonstration, not a production template. It intentionally omits:

- Authentication/authorization
- Pagination and filtering
- Multiple environments (dev/staging/prod)
- Kubernetes deployment
- API versioning
- Rate limiting / caching
