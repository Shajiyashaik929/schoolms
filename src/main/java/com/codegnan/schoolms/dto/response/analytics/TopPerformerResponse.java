package com.codegnan.schoolms.dto.response.analytics;

import lombok.*;

/**
 * Response DTO for GET /api/performance/top-performers
 * Each entry represents one top-scoring mark record, ranked.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopPerformerResponse {

    private Integer rank;
    private StudentRef student;
    private ExamRef exam;
    private SubjectRef subject;
    private Integer highestScore;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentRef {
        private Integer studentId;
        private String fullName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExamRef {
        private String examName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubjectRef {
        private String subjectName;
    }
}
