package com.codegnan.schoolms.controller;

import com.codegnan.schoolms.dto.response.ApiResponse;
import com.codegnan.schoolms.dto.response.analytics.*;
import com.codegnan.schoolms.service.PerformanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private static final Logger log = LoggerFactory.getLogger(PerformanceController.class);

    private final PerformanceService performanceService;

    // Constructor Injection (replacing @RequiredArgsConstructor)
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    /**
     * 5.1 GET /api/performance/students/{studentId}
     * Detailed performance of a student across all subjects and exams.
     */
    @GetMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<StudentFullPerformanceResponse>> getFullPerformanceReport(
            @PathVariable Integer studentId) {

        log.info("GET /api/performance/students/{}", studentId);

        StudentFullPerformanceResponse report =
                performanceService.getFullPerformanceReport(studentId);

        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * 5.2 GET /api/performance/students/{studentId}/exams/{examId}
     * All subject scores for one student in one specific exam.
     */
    @GetMapping("/students/{studentId}/exams/{examId}")
    public ResponseEntity<ApiResponse<StudentExamScoresResponse>> getStudentPerformanceInExam(
            @PathVariable Integer studentId,
            @PathVariable Integer examId) {

        log.info("GET /api/performance/students/{}/exams/{}", studentId, examId);

        StudentExamScoresResponse result =
                performanceService.getStudentPerformanceInExam(studentId, examId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 5.3 GET /api/performance/subjects/averages
     * Calculate average score for every subject across all exams.
     */
    @GetMapping("/subjects/averages")
    public ResponseEntity<ApiResponse<List<SubjectAverageResponse>>> getSubjectAverages() {

        log.info("GET /api/performance/subjects/averages");

        List<SubjectAverageResponse> averages =
                performanceService.getSubjectAverages();

        return ResponseEntity.ok(ApiResponse.success(averages, averages.size()));
    }

    /**
     * 5.4 GET /api/performance/top-performers
     * Returns the students with the highest single score across all exams.
     * Supports optional query parameters:
     *   ?limit={n}   — number of top performers to return (default: 5)
     *   ?examId={id} — scope top performers to a specific exam
     */
    @GetMapping("/top-performers")
    public ResponseEntity<ApiResponse<List<TopPerformerResponse>>> getTopPerformers(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer examId) {

        log.info("GET /api/performance/top-performers — limit={}, examId={}", limit, examId);

        List<TopPerformerResponse> topPerformers =
                performanceService.getTopPerformers(limit, examId);

        return ResponseEntity.ok(ApiResponse.success(topPerformers, topPerformers.size()));
    }

    /**
     * 5.5 GET /api/performance/exams/{examId}/pass-fail
     * Pass/Fail status for all students in a given exam.
     * Supports optional query parameter:
     *   ?passThreshold={n} — minimum score to pass (default: 70)
     */
    @GetMapping("/exams/{examId}/pass-fail")
    public ResponseEntity<ApiResponse<PassFailReportResponse>> getPassFailReport(
            @PathVariable Integer examId,
            @RequestParam(required = false) Integer passThreshold) {

        log.info("GET /api/performance/exams/{}/pass-fail — passThreshold={}", examId, passThreshold);

        PassFailReportResponse report =
                performanceService.getPassFailReport(examId, passThreshold);

        return ResponseEntity.ok(ApiResponse.success(report));
    }
}