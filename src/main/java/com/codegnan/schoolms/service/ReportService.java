package com.codegnan.schoolms.service;

import com.codegnan.schoolms.dto.response.analytics.*;

import java.util.List;

/**
 * Contract for advanced report and leaderboard operations.
 * Implemented by {@link com.codegnan.schoolms.service.impl.ReportServiceImpl}.
 */
public interface ReportService {

    /**
     * Ranks all students by their total score in a specific exam.
     *
     * @param examId the exam's ID
     * @return exam leaderboard DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if the exam does not exist
     */
    ExamLeaderboardResponse getExamLeaderboard(Integer examId);

    /**
     * Returns exams where the sum of all recorded scores meets or exceeds the given threshold.
     *
     * @param minTotalScore minimum total score sum (defaults to 0 if null)
     * @return list of exam summary DTOs
     */
    List<ExamSummaryResponse> getExamSummary(Integer minTotalScore);

    /**
     * Produces a side-by-side performance comparison between two students.
     *
     * @param studentIdA first student's ID
     * @param studentIdB second student's ID
     * @param examId     optional filter to scope comparison to a specific exam
     * @return student comparison DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if either student does not exist
     */
    StudentCompareResponse compareStudents(Integer studentIdA, Integer studentIdB, Integer examId);

    /**
     * Tracks a student's score progression over multiple exam events.
     *
     * @param studentId the student's ID
     * @param subjectId optional filter to scope trend to a specific subject
     * @return student trend DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if the student does not exist
     */
    StudentTrendResponse getStudentTrend(Integer studentId, Integer subjectId);
}
