# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ToDate Backend is a Spring Boot application for managing date courses for couples. It uses JWT authentication, Spring Security, and follows a layered architecture with domain-driven design principles.

**Tech Stack**: Java 21, Spring Boot 3.5.4, Gradle, Spring Data JPA, H2/PostgreSQL, JWT (JJWT 0.12.7)

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Build without tests
./gradlew clean build -x test

# Run the application locally (uses application-local.yml)
./gradlew bootRun

# Run tests
./gradlew test

# Run a specific test class
./gradlew test --tests com.todate.backend.course.service.CourseServiceTest

# Run a specific test method
./gradlew test --tests com.todate.backend.course.service.CourseServiceTest.createCourseTest
```

### Docker
```bash
# Build Docker image (requires ./gradlew build first)
docker build -t todate-backend:latest .

# Run with Docker Compose
docker compose up -d

# View logs
docker compose logs -f

# Stop containers
docker compose down
```

### H2 Console (Local Development)
- Access: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./.local/h2/devdb`
- Username: `sa`
- Password: (empty)

## Architecture

### Layered Architecture
```
Controller Layer (REST API endpoints)
    ↓
Service Layer (@Transactional business logic)
    ↓
Repository Layer (Spring Data JPA)
    ↓
Domain Layer (JPA entities)

DTOs: Separate request/response packages per domain
```

### Domain Structure

The codebase is organized into four main domains under `com.todate.backend`:

**1. Auth Domain** (`auth/`)
- JWT-based authentication system with Google OAuth2 support
- Key classes:
  - `JwtUtil`: Token generation/validation
  - `JwtAuthenticationFilter`: Request filter for JWT validation
  - `SecurityConfig`: Security filter chain configuration
  - `AuthService`: Login/signup logic
  - `GoogleTokenVerifier`: Google ID Token verification (supports multiple Client IDs for Android/iOS)
  - `GoogleOAuth2Props`: Configuration for Google OAuth Client IDs
- Authentication methods:
  - Google OAuth2: ID Token verification with multi-platform support (Android/iOS)
  - Auto-registration on first Google login
- Endpoints: `/auth/google/login` (no authentication required)

**2. User Domain** (`user/`)
- User management and couple relationships
- Key entities:
  - `User`: Main user entity (implements `UserDetails`)
  - `Relationship`: One-to-one couple relationship (partner1/partner2)
- Key features:
  - Password change with policy validation
  - Partner relationship management
- Endpoints: `/user` (requires authentication)

**3. Course Domain** (`course/`)
- Date course management
- Key entities:
  - `Course`: Date course with name, date, isHistory flag
  - `UserCourse`: Many-to-many join table between User and Course
- Key features:
  - Create course (auto-generates name and date)
  - List all courses for a user
  - Delete course (with ownership verification)
- Endpoints: `/course` (requires authentication)

**4. Spot Domain** (`spot/`)
- Location/place management within courses (IN DEVELOPMENT)
- Entity: `Spot`: Individual locations within a course (seq for ordering)
- Currently incomplete - planned integration with external map APIs

### Security Model

- **Authentication**: JWT (access + refresh tokens)
- **Authorization**: All endpoints except `/auth/**` and `/error` require authentication
- **Password**: BCrypt encoded
- **Session**: Stateless (no server-side sessions)
- **Token Flow**:
  1. User logs in via `/auth/login`
  2. Receives access and refresh tokens
  3. Includes `Authorization: Bearer <token>` header in subsequent requests
  4. `JwtAuthenticationFilter` validates token before reaching controllers

### Entity Relationships

```
User (username PK)
  ├─ 1:1 → Relationship (enforces couple pairing via unique constraints)
  └─ M:N → UserCourse → Course (id, name, date, isHistory)
                           └─ 1:M → Spot (locations within course)
```

Important:
- Each User can have at most one Relationship (partner1/partner2 are unique)
- UserCourse join table allows multiple users to share the same Course
- Course ownership is validated before deletion in `CourseService.DeleteCourse()`

## Configuration

### Profiles
- `local`: Default profile for development (H2 database)
- `prod`: Production profile (PostgreSQL, configured via environment variables)

### Environment Variables (Production)
Required in `.env` file or environment:
- `DB_URL`: PostgreSQL connection string
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing key (must be >=32 bytes)
- `JWT_ACCESS_VALIDITY`: Access token duration (e.g., "1d")
- `JWT_REFRESH_VALIDITY`: Refresh token duration (e.g., "1d")
- `GOOGLE_CLIENT_IDS`: Comma-separated Google OAuth Client IDs for Android/iOS (e.g., "android-id,ios-id")
- `MAIL_ID`: Email service ID (future use)
- `MAIL_PASSWORD`: Email service password (future use)

### Application Configuration
Configuration files in `src/main/resources/`:
- `application.yml`: Base configuration (sets default profile to `local`)
- `application-local.yml`: H2 database, create-drop DDL, console enabled
- `application-prod.yml`: PostgreSQL configuration, validate DDL

## Code Patterns and Conventions

### Method Naming
- **Inconsistency exists**: Some service methods use PascalCase (e.g., `CreateCourse`, `DeleteCourse`) while others use camelCase (e.g., `findAllCourses`)
- When adding new methods, prefer camelCase for consistency with Java conventions
- When modifying existing methods, maintain the current naming style of that file

### Exception Handling
- Use `ResponseStatusException` with HTTP status codes
- Example: `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")`
- Custom error messages are included in response body

### Transaction Management
- Service layer methods use `@Transactional` for operations involving multiple repository calls
- Read-only operations may use `@Transactional(readOnly = true)` for optimization

### DTO Pattern
- Request DTOs in `dto/request/` packages
- Response DTOs in `dto/response/` packages
- Use `@Valid` on controller parameters for validation

### Lombok Usage
- Prefer `@Getter`, `@Setter` on entities and DTOs
- Use `@RequiredArgsConstructor` for dependency injection
- `@Builder` pattern for complex object creation
- Protected no-arg constructors on entities (`@NoArgsConstructor(access = AccessLevel.PROTECTED)`)

## Testing

### Test Structure
- Integration tests: `@SpringBootTest` with `@Transactional` for rollback
- Test location: `src/test/java/com/todate/backend/{domain}/`
- Use AssertJ assertions (`assertThat`) for fluent testing

### Running Tests
```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests com.todate.backend.course.service.CourseServiceTest

# With detailed output
./gradlew test --info
```

### Test Coverage Status
- ✅ Course domain: Well-tested (service and integration tests)
- ❌ Auth domain: No tests
- ❌ User domain: No tests
- ❌ Spot domain: No tests (feature incomplete)

## CI/CD

### GitHub Actions Workflow
- **Trigger**: Push to `dev` branch
- **Build**: Gradle build (skips tests with `-x test`)
- **Docker**: Builds and pushes image to Docker Hub
- **Deploy**: Automatically deploys to GCP via SSH
- **Secrets required**: `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `GCP_HOST`, `GCP_USERNAME`, `GCP_SSH_KEY`, plus all environment variables

### Deployment Process
1. Push to `dev` branch triggers workflow
2. Gradle builds JAR file
3. Docker image built and pushed to Docker Hub
4. `docker-compose.yml` copied to GCP server
5. `.env` file created with secrets
6. Containers restarted with new image

## Known Issues and Improvement Areas

### Active Development
- Spot domain is incomplete (skeleton only)
- External map API integration pending

### Code Quality
- Method naming inconsistency (PascalCase vs camelCase in services)
- Test coverage gaps (Auth, User, Spot domains)
- No global exception handler (`@ControllerAdvice`)
- Password policy uses `AllowAllPasswordPolicy` (needs strengthening)

### Security Considerations
- JWT secret should not be hardcoded in yml files (use environment variables)
- CORS configuration needs review before production
- Consider implementing rate limiting
- Refresh token rotation not yet implemented

## Working with This Codebase

### Adding a New Feature
1. Identify the domain (auth/user/course/spot) or create new domain package
2. Create entity in `domain/` package with JPA annotations
3. Create repository interface extending `JpaRepository`
4. Create service class with `@Service` and `@Transactional` logic
5. Create DTOs in `dto/request/` and `dto/response/`
6. Create controller with `@RestController` and mappings
7. Write tests in `src/test/java/com/todate/backend/{domain}/`

### Modifying Security Rules
- Edit `SecurityConfig.securityFilterChain()` method
- Add endpoints to `.requestMatchers()` for permit/authenticate
- JWT validation logic is in `JwtAuthenticationFilter`

### Database Schema Changes
- Modify entity classes with JPA annotations
- Local profile uses `create-drop` so schema auto-updates
- Production uses `validate` so schema must be manually migrated

### Adding New Endpoints
- Follow REST conventions (GET/POST/PUT/DELETE/PATCH)
- Use appropriate HTTP status codes
- Include authentication where needed (exclude via SecurityConfig)
- Create DTOs for request/response bodies
- Add `@Valid` for input validation