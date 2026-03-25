package com.codegnan.schoolms.dto.response.analytics;

import lombok.*;
import java.util.List;

/**
 * Response DTO for GET /api/performance/students/{studentId}
 * Full performance report for a student across all exams and subjects.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentFullPerformanceResponse {

    private StudentDetail student;
    private Double overallAverage;
    private Integer totalExamsAppeared;
    private List<ExamPerformance> performance;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentDetail {
        private Integer studentId;
        private String fullName;
        private Integer enrollmentYear;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExamPerformance {
        private Integer examId;
        private String examName;
        private String examDate;
        private List<SubjectScore> subjects;
        private Integer examTotal;
        private Double examAverage;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubjectScore {
        private String subjectName;
        private Integer score;
        private String status;
    }
}
