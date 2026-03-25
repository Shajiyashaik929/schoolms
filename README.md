# School Management System — REST API

A production-grade Spring Boot application that exposes **29 RESTful endpoints** for managing students, subjects, exams, marks, and advanced performance analytics.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.x |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL 8.x |
| Validation | Jakarta Bean Validation |
| Build Tool | Apache Maven |
| Boilerplate | Lombok |

---

## Prerequisites

- Java 17+ installed
- MySQL 8.x running locally
- Maven 3.8+

---

## Database Setup

Create the database before starting the application:

```sql
CREATE DATABASE schoolms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Tables are created automatically by Hibernate (`ddl-auto=update`) on first startup.

---

## Configuration

Open `src/main/resources/application.properties` and update your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/schoolms_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password_here
```

---

## Running the Application

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Or run the JAR directly
java -jar target/schoolms-1.0.0.jar
```

The server starts on **http://localhost:8080**

---

## API Endpoint Reference

### Students `/api/students`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/students` | List all students |
| GET | `/api/students/{studentId}` | Get student by ID |
| POST | `/api/students` | Create new student |
| PUT | `/api/students/{studentId}` | Update student |
| DELETE | `/api/students/{studentId}` | Delete student |

### Subjects `/api/subjects`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/subjects` | List all subjects |
| GET | `/api/subjects/{subjectId}` | Get subject by ID |
| POST | `/api/subjects` | Create new subject |
| PUT | `/api/subjects/{subjectId}` | Update subject |
| DELETE | `/api/subjects/{subjectId}` | Delete subject |

### Exams `/api/exams`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/exams` | List all exams (supports `?sort=date`) |
| GET | `/api/exams/{examId}` | Get exam by ID |
| POST | `/api/exams` | Schedule new exam |
| PUT | `/api/exams/{examId}` | Update exam |
| DELETE | `/api/exams/{examId}` | Delete exam |

### Marks `/api/marks`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/marks` | List marks (supports `?studentId`, `?subjectId`, `?examId`, `?minScore`) |
| GET | `/api/marks/{markId}` | Get mark by ID |
| POST | `/api/marks` | Record new mark |
| PUT | `/api/marks/{markId}` | Correct a mark score |
| DELETE | `/api/marks/{markId}` | Delete a mark |

### Performance Analytics `/api/performance`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/performance/students/{studentId}` | Full performance report |
| GET | `/api/performance/students/{studentId}/exams/{examId}` | Student in specific exam |
| GET | `/api/performance/subjects/averages` | Subject-wise averages |
| GET | `/api/performance/top-performers` | Global top performers (`?limit`, `?examId`) |
| GET | `/api/performance/exams/{examId}/pass-fail` | Pass/Fail report (`?passThreshold`) |

### Reports & Leaderboards `/api/reports`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/exams/{examId}/leaderboard` | Exam leaderboard |
| GET | `/api/reports/exams/summary` | Exam total score summary (`?minTotalScore`) |
| GET | `/api/reports/students/compare` | Compare two students (`?studentIdA`, `?studentIdB`, `?examId`) |
| GET | `/api/reports/students/{studentId}/trend` | Score trend (`?subjectId`) |

---

## Request / Response Conventions

### Success Response (list)
```json
{
  "status": "success",
  "data": [...],
  "totalCount": 4
}
```

### Success Response (single)
```json
{
  "status": "success",
  "data": { ... }
}
```

### Success Response (create/update)
```json
{
  "status": "success",
  "message": "Student created successfully.",
  "data": { ... }
}
```

### Success Response (delete)
```json
{
  "status": "success",
  "message": "Student with ID 5 has been deleted successfully."
}
```

### Error Response
```json
{
  "status": "error",
  "errorCode": "STUDENT_NOT_FOUND",
  "message": "Student with ID 99 does not exist.",
  "timestamp": "2025-03-25T10:30:00Z",
  "path": "/api/students/99"
}
```

---

## HTTP Status Code Reference

| Status | Meaning |
|---|---|
| 200 OK | Request succeeded |
| 201 Created | Resource created successfully |
| 400 Bad Request | Malformed request or missing parameters |
| 404 Not Found | Requested resource does not exist |
| 409 Conflict | Duplicate entry or referential integrity violation |
| 422 Unprocessable Entity | Validation errors on request data |
| 500 Internal Server Error | Unexpected server-side error |

---

## Error Codes

| Error Code | HTTP Status | Description |
|---|---|---|
| `STUDENT_NOT_FOUND` | 404 | Student with given ID does not exist |
| `SUBJECT_NOT_FOUND` | 404 | Subject with given ID does not exist |
| `EXAM_NOT_FOUND` | 404 | Exam with given ID does not exist |
| `MARK_NOT_FOUND` | 404 | Mark with given ID does not exist |
| `DUPLICATE_MARK` | 409 | A mark already exists for this Student+Subject+Exam combination |
| `STUDENT_HAS_MARKS` | 409 | Cannot delete student — has associated mark records |
| `SUBJECT_HAS_MARKS` | 409 | Cannot delete subject — has associated mark records |
| `EXAM_HAS_MARKS` | 409 | Cannot delete exam — has associated mark records |
| `VALIDATION_ERROR` | 422 | One or more request fields failed validation |
| `MALFORMED_REQUEST` | 400 | Request body is missing or malformed JSON |
| `MISSING_PARAMETER` | 400 | A required query parameter is absent |
| `TYPE_MISMATCH` | 400 | Path variable or query param has wrong type |
| `INTERNAL_SERVER_ERROR` | 500 | Unexpected server-side error |

---

## Score Validation Rules

- Score must be between **0** and **100** inclusive.
- A score of **70 or above** is considered **Pass** (default threshold).
- Pass threshold is configurable per request on the pass/fail endpoint.

---

## Project Structure

```
schoolms/
├── src/main/java/com/codegnan/schoolms/
│   ├── SchoolMsApplication.java          # Entry point
│   ├── controller/                        # REST layer — HTTP in/out only
│   │   ├── StudentController.java
│   │   ├── SubjectController.java
│   │   ├── ExamController.java
│   │   ├── MarkController.java
│   │   ├── PerformanceController.java
│   │   └── ReportController.java
│   ├── service/                           # Business logic layer
│   │   ├── StudentService.java
│   │   ├── SubjectService.java
│   │   ├── ExamService.java
│   │   ├── MarkService.java
│   │   ├── PerformanceService.java
│   │   └── ReportService.java
│   ├── repository/                        # Data access layer (Spring Data JPA)
│   │   ├── StudentRepository.java
│   │   ├── SubjectRepository.java
│   │   ├── ExamRepository.java
│   │   └── MarkRepository.java
│   ├── entity/                            # JPA entities (DB table mapping)
│   │   ├── Student.java
│   │   ├── Subject.java
│   │   ├── Exam.java
│   │   └── Mark.java
│   ├── dto/
│   │   ├── request/                       # Inbound request payloads
│   │   │   ├── StudentRequest.java
│   │   │   ├── SubjectRequest.java
│   │   │   ├── ExamRequest.java
│   │   │   ├── MarkRequest.java
│   │   │   └── MarkUpdateRequest.java
│   │   └── response/                      # Outbound response objects
│   │       ├── ApiResponse.java           # Generic success wrapper
│   │       ├── ErrorResponse.java         # Standard error envelope
│   │       ├── StudentResponse.java
│   │       ├── SubjectResponse.java
│   │       ├── ExamResponse.java
│   │       ├── MarkResponse.java
│   │       └── analytics/                 # Complex analytics DTOs
│   │           ├── StudentFullPerformanceResponse.java
│   │           ├── StudentExamScoresResponse.java
│   │           ├── SubjectAverageResponse.java
│   │           ├── TopPerformerResponse.java
│   │           ├── PassFailReportResponse.java
│   │           ├── ExamLeaderboardResponse.java
│   │           ├── ExamSummaryResponse.java
│   │           ├── StudentCompareResponse.java
│   │           └── StudentTrendResponse.java
│   └── exception/                         # Exception layer
│       ├── ResourceNotFoundException.java
│       ├── DuplicateMarkException.java
│       ├── ResourceConflictException.java
│       └── GlobalExceptionHandler.java    # @RestControllerAdvice
├── src/main/resources/
│   └── application.properties
├── src/test/
│   └── SchoolMsApplicationTests.java
└── pom.xml
```
