package com.codegnan.schoolms.dto.response.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;

/**
 * Response DTO for GET /api/reports/students/{studentId}/trend
 * Score progression over multiple exams for a student, optionally scoped to a subject.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentTrendResponse {

    private StudentRef student;
    private SubjectRef subject;   // null when not filtered by a specific subject
    private List<TrendEntry> trend;
    private String trendDirection;
    private Double changePercent;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentRef {
        private Integer studentId;
        private String fullName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubjectRef {
        private Integer subjectId;
        private String subjectName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TrendEntry {
        private Integer examId;
        private String examName;
        private String examDate;
        private Integer score;
    }
}
