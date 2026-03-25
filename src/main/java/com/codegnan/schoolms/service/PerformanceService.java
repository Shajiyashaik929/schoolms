package com.codegnan.schoolms.service;

import com.codegnan.schoolms.dto.response.analytics.*;

import java.util.List;

/**
 * Contract for performance analytics operations.
 * Implemented by {@link com.codegnan.schoolms.service.impl.PerformanceServiceImpl}.
 */
public interface PerformanceService {

    /**
     * Builds a full performance report for a student across all exams and subjects.
     *
     * @param studentId the student's ID
     * @return complete performance report DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if the student does not exist
     */
    StudentFullPerformanceResponse getFullPerformanceReport(Integer studentId);

    /**
     * Returns all subject scores for one student in one specific exam.
     *
     * @param studentId the student's ID
     * @param examId    the exam's ID
     * @return exam-scoped performance DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if either entity does not exist
     */
    StudentExamScoresResponse getStudentPerformanceInExam(Integer studentId, Integer examId);

    /**
     * Calculates the average score for every subject across all recorded marks.
     *
     * @return list of subject-average response DTOs
     */
    List<SubjectAverageResponse> getSubjectAverages();

    /**
     * Returns the students with the highest single-subject scores.
     *
     * @param limit  maximum number of performers to return (defaults to 5 if null)
     * @param examId optional filter to scope results to a specific exam
     * @return ranked list of top-performer response DTOs
     */
    List<TopPerformerResponse> getTopPerformers(Integer limit, Integer examId);

    /**
     * Produces a pass/fail report for all students in a given exam.
     *
     * @param examId        the exam's ID
     * @param passThreshold minimum score to be considered a pass (defaults to 70 if null)
     * @return pass/fail report DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if the exam does not exist
     */
    PassFailReportResponse getPassFailReport(Integer examId, Integer passThreshold);
}
