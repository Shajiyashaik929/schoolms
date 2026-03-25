package com.codegnan.schoolms.dto.response.analytics;

import lombok.*;

/**
 * Response DTO for GET /api/performance/subjects/averages
 * Aggregate statistics per subject across all exams.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAverageResponse {

    private Integer subjectId;
    private String subjectName;
    private Double averageScore;
    private Long totalMarksRecorded;
    private Integer highestScore;
    private Integer lowestScore;
}
