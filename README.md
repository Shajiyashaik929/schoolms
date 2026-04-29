School Management System REST API

This is a production-grade Spring Boot application that provides REST APIs to manage students, subjects, exams, marks, and performance analytics.

Technology Stack

Java 17
Spring Boot 3
Spring Data JPA
Hibernate
MySQL
Maven
Lombok

Prerequisites

Java 17 or above
MySQL installed and running
Maven installed

Database Setup

Create database before running the application:

CREATE DATABASE schoolms_db;

Tables will be created automatically using Hibernate.

Configuration

Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/schoolms_db
spring.datasource.username=root
spring.datasource.password=your_password

Running the Application

mvn clean package -DskipTests
mvn spring-boot:run

Application will start on:

http://localhost:8080

API Overview

This project provides 29 REST endpoints for managing school operations. Full API details are documented here: 

Students API

GET /api/students
GET /api/students/{studentId}
POST /api/students
PUT /api/students/{studentId}
DELETE /api/students/{studentId}

Subjects API

GET /api/subjects
GET /api/subjects/{subjectId}
POST /api/subjects
PUT /api/subjects/{subjectId}
DELETE /api/subjects/{subjectId}

Exams API

GET /api/exams
GET /api/exams/{examId}
POST /api/exams
PUT /api/exams/{examId}
DELETE /api/exams/{examId}

Marks API

GET /api/marks
GET /api/marks/{markId}
POST /api/marks
PUT /api/marks/{markId}
DELETE /api/marks/{markId}

Performance APIs

GET /api/performance/students/{studentId}
GET /api/performance/students/{studentId}/exams/{examId}
GET /api/performance/subjects/averages
GET /api/performance/top-performers
GET /api/performance/exams/{examId}/pass-fail

Reports APIs

GET /api/reports/exams/{examId}/leaderboard
GET /api/reports/exams/summary
GET /api/reports/students/compare
GET /api/reports/students/{studentId}/trend

Response Format

Success Response

{
"status": "success",
"data": {}
}

Error Response

{
"status": "error",
"errorCode": "ERROR_CODE",
"message": "Error message"
}

HTTP Status Codes

200 OK
201 Created
400 Bad Request
404 Not Found
409 Conflict
422 Validation Error
500 Internal Server Error

Project Structure

schoolms

src/main/java/com/codegnan/schoolms

SchoolMsApplication.java

controller
StudentController.java
SubjectController.java
ExamController.java
MarkController.java
PerformanceController.java
ReportController.java

service
interfaces and implementations

repository
StudentRepository.java
SubjectRepository.java
ExamRepository.java
MarkRepository.java

entity
Student.java
Subject.java
Exam.java
Mark.java

dto
request and response classes

exception
ResourceNotFoundException.java
DuplicateMarkException.java
ResourceConflictException.java
GlobalExceptionHandler.java

Future Enhancements

Add Spring Security with JWT
Dockerize application
Deploy on GCP or AWS
Add CI CD pipeline

Author

Shajiya Shaik
Java Full Stack Developer and GCP Data Engineer
