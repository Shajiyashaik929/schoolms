package com.codegnan.schoolms.controller;

import com.codegnan.schoolms.dto.request.StudentRequest;
import com.codegnan.schoolms.dto.response.ApiResponse;
import com.codegnan.schoolms.dto.response.StudentResponse;
import com.codegnan.schoolms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 1.1 GET /api/students
     * Retrieve a list of all registered students.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        log.info("GET /api/students");
        List<StudentResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success(students, students.size()));
    }

    /**
     * 1.2 GET /api/students/{studentId}
     * Retrieve a single student by their unique ID.
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable Integer studentId) {
        log.info("GET /api/students/{}", studentId);
        StudentResponse student = studentService.getStudentById(studentId);
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    /**
     * 1.3 POST /api/students
     * Register a new student in the system.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentRequest request) {
        log.info("POST /api/students");
        StudentResponse created = studentService.createStudent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully.", created));
    }

    /**
     * 1.4 PUT /api/students/{studentId}
     * Update personal details of an existing student.
     */
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Integer studentId,
            @Valid @RequestBody StudentRequest request) {
        log.info("PUT /api/students/{}", studentId);
        StudentResponse updated = studentService.updateStudent(studentId, request);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully.", updated));
    }

    /**
     * 1.5 DELETE /api/students/{studentId}
     * Permanently delete a student record.
     */
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Integer studentId) {
        log.info("DELETE /api/students/{}", studentId);
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok(
                ApiResponse.success("Student with ID " + studentId + " has been deleted successfully."));
    }
}
