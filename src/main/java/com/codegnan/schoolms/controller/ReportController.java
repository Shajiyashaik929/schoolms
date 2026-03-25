package com.codegnan.schoolms.controller;

import com.codegnan.schoolms.dto.response.ApiResponse;
import com.codegnan.schoolms.dto.response.analytics.*;
import com.codegnan.schoolms.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 6.1 GET /api/reports/exams/{examId}/leaderboard
     * Rank all students by their total score in a specific exam.
     */
    @GetMapping("/exams/{examId}/leaderboard")
    public ResponseEntity<ApiResponse<ExamLeaderboardResponse>> getExamLeaderboard(
            @PathVariable Integer examId) {
        log.info("GET /api/reports/exams/{}/leaderboard", examId);
        ExamLeaderboardResponse leaderboard = reportService.getExamLeaderboard(examId);
        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    /**
     * 6.2 GET /api/reports/exams/summary
     * Returns exams where the total score sum exceeds a given threshold.
     * Supports optional query parameter:
     *   ?minTotalScore={n} — only return exams where sum of scores >= this value (default: 0)
     */
    @GetMapping("/exams/summary")
    public ResponseEntity<ApiResponse<List<ExamSummaryResponse>>> getExamSummary(
            @RequestParam(required = false) Integer minTotalScore) {
        log.info("GET /api/reports/exams/summary — minTotalScore={}", minTotalScore);
        List<ExamSummaryResponse> summary = reportService.getExamSummary(minTotalScore);
        return ResponseEntity.ok(ApiResponse.success(summary, summary.size()));
    }

    /**
     * 6.3 GET /api/reports/students/compare
     * Side-by-side performance comparison between two students.
     * Required query parameters:
     *   ?studentIdA={id} — first student ID
     *   ?studentIdB={id} — second student ID
     * Optional:
     *   ?examId={id}     — limit comparison to a specific exam
     */
    @GetMapping("/students/compare")
    public ResponseEntity<ApiResponse<StudentCompareResponse>> compareStudents(
            @RequestParam Integer studentIdA,
            @RequestParam Integer studentIdB,
            @RequestParam(required = false) Integer examId) {
        log.info("GET /api/reports/students/compare — studentIdA={}, studentIdB={}, examId={}",
                studentIdA, studentIdB, examId);
        StudentCompareResponse comparison = reportService.compareStudents(studentIdA, studentIdB, examId);
        return ResponseEntity.ok(ApiResponse.success(comparison));
    }

    /**
     * 6.4 GET /api/reports/students/{studentId}/trend
     * Track a student's score progression over multiple exam events.
     * Optional query parameter:
     *   ?subjectId={id} — scope trend to a specific subject
     */
    @GetMapping("/students/{studentId}/trend")
    public ResponseEntity<ApiResponse<StudentTrendResponse>> getStudentTrend(
            @PathVariable Integer studentId,
            @RequestParam(required = false) Integer subjectId) {
        log.info("GET /api/reports/students/{}/trend — subjectId={}", studentId, subjectId);
        StudentTrendResponse trend = reportService.getStudentTrend(studentId, subjectId);
        return ResponseEntity.ok(ApiResponse.success(trend));
    }
}
