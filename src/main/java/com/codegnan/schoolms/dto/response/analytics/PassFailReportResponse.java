package com.codegnan.schoolms.dto.response.analytics;

import lombok.*;
import java.util.List;

/**
 * Response DTO for GET /api/performance/exams/{examId}/pass-fail
 * Pass/Fail status for all students in a given exam.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassFailReportResponse {

    private ExamRef exam;
    private Integer passThreshold;
    private Summary summary;
    private List<StudentResult> results;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExamRef {
        private Integer examId;
        private String examName;
        private String examDate;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Summary {
        private Integer totalStudents;
        private Integer passed;
        private Integer failed;
        private Double passPercentage;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentResult {
        private StudentRef student;
        private List<SubjectResult> subjectResults;
        private String overallStatus;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentRef {
        private Integer studentId;
        private String fullName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubjectResult {
        private String subjectName;
        private Integer score;
        private String status;
    }
}
