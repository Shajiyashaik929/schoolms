# School Management System — Architecture Document

## 1. Overview

The School Management System (SchoolMS) is a **production-grade Spring Boot 3.2 REST API** that manages students, subjects, exams, marks, and performance analytics. It follows a strict layered architecture with clear separation of concerns at every tier.

---

## 2. Technology Stack

| Concern | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.x |
| Web layer | Spring MVC (embedded Tomcat) |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL 8.x (production), H2 (test) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI 3 / Swagger UI |
| Monitoring | Spring Boot Actuator |
| Build | Apache Maven |
| Boilerplate | Lombok |
| Testing | JUnit 5 + Mockito + MockMvc |

---

## 3. Layered Architecture

```
HTTP Client
    │
    ▼
┌─────────────────────────────────────────────────────┐
│  Controller Layer  (com.codegnan.schoolms.controller)│
│  - Receives and validates HTTP requests              │
│  - Delegates to Service layer                        │
│  - Returns standardised ApiResponse<T> envelopes     │
│  - Never contains business logic                     │
└─────────────────────────┬───────────────────────────┘
                          │  calls interface
                          ▼
┌─────────────────────────────────────────────────────┐
│  Service Interface  (com.codegnan.schoolms.service)  │
│  - Defines the contract (Javadoc on each method)     │
│  - Decouples callers from implementation details     │
└─────────────────────────┬───────────────────────────┘
                          │  implemented by
                          ▼
┌─────────────────────────────────────────────────────┐
│  Service Impl  (com.codegnan.schoolms.service.impl)  │
│  - Contains all business logic and rules             │
│  - Manages transactions (@Transactional)             │
│  - Maps entities ↔ DTOs (no MapStruct / ModelMapper) │
│  - Validates cross-entity constraints                │
└─────────────────────────┬───────────────────────────┘
                          │  calls
                          ▼
┌─────────────────────────────────────────────────────┐
│  Repository Layer  (com.codegnan.schoolms.repository)│
│  - Spring Data JPA interfaces                        │
│  - Custom JPQL queries with JOIN FETCH               │
│  - No SQL is written manually                        │
└─────────────────────────┬───────────────────────────┘
                          │  ORM mapping
                          ▼
┌─────────────────────────────────────────────────────┐
│  Entity Layer  (com.codegnan.schoolms.entity)        │
│  - JPA-annotated domain classes                      │
│  - @ManyToOne(fetch = LAZY) on all FK relations      │
│  - @UniqueConstraint on marks(student, subject, exam)│
└─────────────────────────┬───────────────────────────┘
                          │
                          ▼
                    MySQL 8.x Database
```

---

## 4. Package Structure

```
com.codegnan.schoolms
├── SchoolMsApplication.java         ← Spring Boot entry point
│
├── config/
│   ├── OpenApiConfig.java           ← SpringDoc / Swagger UI bean
│   └── WebConfig.java               ← CORS mappings
│
├── constants/
│   └── ErrorCodes.java              ← Centralised error code constants
│
├── controller/
│   ├── StudentController.java        ← GET/POST/PUT/DELETE /api/students
│   ├── SubjectController.java        ← GET/POST/PUT/DELETE /api/subjects
│   ├── ExamController.java           ← GET/POST/PUT/DELETE /api/exams
│   ├── MarkController.java           ← GET/POST/PUT/DELETE /api/marks
│   ├── PerformanceController.java    ← GET /api/performance/**
│   └── ReportController.java         ← GET /api/reports/**
│
├── service/                         ← Interfaces (contracts)
│   ├── StudentService.java
│   ├── SubjectService.java
│   ├── ExamService.java
│   ├── MarkService.java
│   ├── PerformanceService.java
│   ├── ReportService.java
│   └── impl/                        ← Implementations
│       ├── StudentServiceImpl.java
│       ├── SubjectServiceImpl.java
│       ├── ExamServiceImpl.java
│       ├── MarkServiceImpl.java
│       ├── PerformanceServiceImpl.java
│       └── ReportServiceImpl.java
│
├── repository/
│   ├── StudentRepository.java
│   ├── SubjectRepository.java
│   ├── ExamRepository.java
│   └── MarkRepository.java          ← 15+ JPQL queries
│
├── entity/
│   ├── Student.java
│   ├── Subject.java
│   ├── Exam.java
│   └── Mark.java
│
├── dto/
│   ├── request/
│   │   ├── StudentRequest.java
│   │   ├── SubjectRequest.java
│   │   ├── ExamRequest.java
│   │   ├── MarkRequest.java
│   │   └── MarkUpdateRequest.java
│   └── response/
│       ├── ApiResponse.java         ← Generic success wrapper
│       ├── ErrorResponse.java       ← Standard error envelope
│       ├── StudentResponse.java
│       ├── SubjectResponse.java
│       ├── ExamResponse.java
│       ├── MarkResponse.java
│       └── analytics/               ← Complex analytics DTOs
│           ├── StudentFullPerformanceResponse.java
│           ├── StudentExamScoresResponse.java
│           ├── SubjectAverageResponse.java
│           ├── TopPerformerResponse.java
│           ├── PassFailReportResponse.java
│           ├── ExamLeaderboardResponse.java
│           ├── ExamSummaryResponse.java
│           ├── StudentCompareResponse.java
│           └── StudentTrendResponse.java
│
└── exception/
    ├── ResourceNotFoundException.java
    ├── DuplicateMarkException.java
    ├── ResourceConflictException.java
    └── GlobalExceptionHandler.java  ← @RestControllerAdvice
```

---

## 5. Key Design Decisions

### 5.1 Service Interface + Implementation Pattern

Each service is split into a public interface (in `service/`) and a concrete implementation (in `service/impl/`). This enables:
- Compile-time decoupling — callers depend only on the contract
- Easier unit testing with Mockito mocks against the interface
- Future extensibility (swap implementations without touching callers)

### 5.2 No MapStruct / ModelMapper

Entity-to-DTO mapping is done manually in private `toXxxResponse()` helper methods inside each `ServiceImpl`. This keeps the dependency graph minimal and makes every field mapping explicit and easy to audit.

### 5.3 Centralised Error Codes

All error codes are static final constants in `constants/ErrorCodes.java`. Exception classes and the `GlobalExceptionHandler` reference these constants — no magic strings scattered across the codebase.

### 5.4 N+1 Prevention

All repository queries that traverse associations use `JOIN FETCH` in JPQL (e.g. `findByIdWithDetails`, `findByStudentIdWithDetails`). This ensures Hibernate loads all needed data in a single SQL statement instead of issuing one query per row.

### 5.5 Referential Integrity on Delete

Before deleting a Student, Subject, or Exam, the service checks whether any Mark records reference it (via `markRepository.count*()`) and throws `ResourceConflictException` with an explanatory message. This produces a clean 409 API response rather than an opaque database constraint error.

### 5.6 Duplicate Mark Detection

The application enforces uniqueness of (student_id, subject_id, exam_id) at two levels:
- **Application level**: `markRepository.existsBy...()` check before `save()` → produces `DUPLICATE_MARK` 409
- **Database level**: `@UniqueConstraint` on the `marks` table → last-resort guard

### 5.7 Optional JPQL Parameters

The `findWithFilters` repository query uses `(:param IS NULL OR field = :param)` predicates so a single method handles all combinations of optional mark filters without needing dynamic queries.

### 5.8 Transaction Strategy

- `@Transactional(readOnly = true)` on all read operations — allows connection pool optimisations
- `@Transactional` (read-write) on all mutating operations
- No transaction annotations on controller layer

### 5.9 Response Envelope

Every successful response is wrapped in a generic `ApiResponse<T>` with:
- `status: "success"`
- `data: <T>` (excluded when null via `@JsonInclude(NON_NULL)`)
- `totalCount: <n>` (included only on list responses)
- `message: <string>` (included only on create/update/delete responses)

Every error response follows the `ErrorResponse` schema with `status`, `errorCode`, `message`, `timestamp`, and `path`.

---

## 6. Database Schema

```
students
  student_id    INT  PK AUTO_INCREMENT
  first_name    VARCHAR
  last_name     VARCHAR
  enrollment_year INT

subjects
  subject_id    INT  PK AUTO_INCREMENT
  subject_name  VARCHAR

exams
  exam_id       INT  PK AUTO_INCREMENT
  exam_name     VARCHAR
  exam_date     DATE

marks
  mark_id       INT  PK AUTO_INCREMENT
  student_id    INT  FK → students.student_id
  subject_id    INT  FK → subjects.subject_id
  exam_id       INT  FK → exams.exam_id
  score         INT  (0–100)
  UNIQUE (student_id, subject_id, exam_id)
```

Tables are created and maintained automatically by Hibernate (`ddl-auto=update`). For production, migrate to `validate` after the initial setup.

---

## 7. API Endpoints Summary

| Group | Count | Base Path |
|---|---|---|
| Students | 5 | `/api/students` |
| Subjects | 5 | `/api/subjects` |
| Exams | 5 | `/api/exams` |
| Marks | 5 | `/api/marks` |
| Performance Analytics | 5 | `/api/performance` |
| Reports & Leaderboards | 4 | `/api/reports` |
| **Total** | **29** | |

Full endpoint documentation is available in the interactive Swagger UI at `/swagger-ui.html` when the application is running.

---

## 8. Testing Strategy

| Layer | Approach | Tooling |
|---|---|---|
| Controller | `@WebMvcTest` + `@MockBean` service | MockMvc, Mockito |
| Service | `@ExtendWith(MockitoExtension.class)` + `@InjectMocks` impl | Mockito, AssertJ |
| Integration | `@SpringBootTest` + `@ActiveProfiles("test")` | H2 in-memory DB |

The test profile (`application-test.properties`) replaces MySQL with an H2 in-memory database in MySQL compatibility mode. No external infrastructure is required to run tests.

---

## 9. Observability

- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI JSON**: `/v3/api-docs`
- **Health check**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Application log**: `logs/schoolms.log`
