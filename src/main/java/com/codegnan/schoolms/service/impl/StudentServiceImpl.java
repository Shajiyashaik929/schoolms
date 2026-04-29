package com.codegnan.schoolms.service.impl;

import com.codegnan.schoolms.constants.ErrorCodes;
import com.codegnan.schoolms.dto.request.StudentRequest;
import com.codegnan.schoolms.dto.response.StudentResponse;
import com.codegnan.schoolms.entity.Student;
import com.codegnan.schoolms.exception.ResourceConflictException;
import com.codegnan.schoolms.exception.ResourceNotFoundException;
import com.codegnan.schoolms.repository.MarkRepository;
import com.codegnan.schoolms.repository.StudentRepository;
import com.codegnan.schoolms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final MarkRepository markRepository;

    // Constructor Injection (replacing @RequiredArgsConstructor)
    public StudentServiceImpl(StudentRepository studentRepository,
                              MarkRepository markRepository) {
        this.studentRepository = studentRepository;
        this.markRepository = markRepository;
    }

    // ------------------------------------------------------------------
    // GET /api/students
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {

        log.debug("Fetching all students");

        return studentRepository.findAll()
                .stream()
                .map(this::toStudentResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // GET /api/students/{studentId}
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Integer studentId) {

        log.debug("Fetching student with ID: {}", studentId);

        return toStudentResponse(findStudentOrThrow(studentId));
    }

    // ------------------------------------------------------------------
    // POST /api/students
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {

        log.info("Creating new student: {} {}", request.getFirstName(), request.getLastName());

        Student student = new Student();
        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEnrollmentYear(request.getEnrollmentYear());

        Student saved = studentRepository.save(student);

        log.info("Student created with ID: {}", saved.getStudentId());

        return toStudentResponse(saved);
    }

    // ------------------------------------------------------------------
    // PUT /api/students/{studentId}
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public StudentResponse updateStudent(Integer studentId, StudentRequest request) {

        log.info("Updating student with ID: {}", studentId);

        Student student = findStudentOrThrow(studentId);

        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEnrollmentYear(request.getEnrollmentYear());

        Student updated = studentRepository.save(student);

        log.info("Student updated: ID={}", studentId);

        return toStudentResponse(updated);
    }

    // ------------------------------------------------------------------
    // DELETE /api/students/{studentId}
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public void deleteStudent(Integer studentId) {

        log.info("Attempting to delete student with ID: {}", studentId);

        Student student = findStudentOrThrow(studentId);

        long markCount = markRepository.countByStudentStudentId(studentId);

        if (markCount > 0) {
            throw new ResourceConflictException(
                    ErrorCodes.STUDENT_HAS_MARKS,
                    "Cannot delete Student with ID " + studentId +
                    " — they have " + markCount + " associated mark record(s). " +
                    "Remove all marks for this student first."
            );
        }

        studentRepository.delete(student);

        log.info("Student with ID {} deleted successfully.", studentId);
    }

    // ------------------------------------------------------------------
    // Helper method
    // ------------------------------------------------------------------

    @Override
    public Student findStudentOrThrow(Integer studentId) {

        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.STUDENT_NOT_FOUND,
                        "Student with ID " + studentId + " does not exist."
                ));
    }

    // ------------------------------------------------------------------
    // Mapping Method
    // ------------------------------------------------------------------

    private StudentResponse toStudentResponse(Student student) {

        String fullName = student.getFirstName() + " " + student.getLastName();

        return new StudentResponse(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEnrollmentYear(),
                fullName
        );
    }
}