package com.codegnan.schoolms.service;

import com.codegnan.schoolms.dto.request.ExamRequest;
import com.codegnan.schoolms.dto.response.ExamResponse;
import com.codegnan.schoolms.entity.Exam;

import java.util.List;

/**
 * Contract for exam CRUD operations.
 * Implemented by {@link com.codegnan.schoolms.service.impl.ExamServiceImpl}.
 */
public interface ExamService {

    /**
     * Returns all exams, optionally sorted by date descending.
     *
     * @param sortByDate when {@code true} results are ordered by examDate DESC
     * @return list of exam response DTOs
     */
    List<ExamResponse> getAllExams(boolean sortByDate);

    /**
     * Retrieves a single exam by primary key.
     *
     * @param examId the exam's ID
     * @return exam response DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if not found
     */
    ExamResponse getExamById(Integer examId);

    /**
     * Creates and persists a new exam record.
     *
     * @param request validated inbound payload
     * @return the created exam as a response DTO
     */
    ExamResponse createExam(ExamRequest request);

    /**
     * Updates an existing exam record.
     *
     * @param examId  the exam's ID
     * @param request validated inbound payload
     * @return the updated exam as a response DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if not found
     */
    ExamResponse updateExam(Integer examId, ExamRequest request);

    /**
     * Deletes an exam record.
     *
     * @param examId the exam's ID
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException  if not found
     * @throws com.codegnan.schoolms.exception.ResourceConflictException if the exam has associated marks
     */
    void deleteExam(Integer examId);

    /**
     * Retrieves the {@link Exam} JPA entity or throws if absent.
     * Shared with MarkService, PerformanceService, and ReportService.
     *
     * @param examId the exam's ID
     * @return the found entity
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if not found
     */
    Exam findExamOrThrow(Integer examId);
}
