package com.codegnan.schoolms.dto.response.analytics;

import lombok.*;

/**
 * Response DTO for GET /api/reports/exams/summary
 * Per-exam total score sum and student count, filtered by a minimum threshold.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamSummaryResponse {

    private Integer examId;
    private String examName;
    private String examDate;
    private Long totalScoreSum;
    private Long studentsAppeared;
}
