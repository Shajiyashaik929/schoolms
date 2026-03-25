package com.codegnan.schoolms.dto.response.analytics;

import lombok.*;
import java.util.List;

/**
 * Response DTO for GET /api/performance/students/{studentId}/exams/{examId}
 * All subject scores for one student in one specific exam.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamScoresResponse {

    private StudentRef student;
    private ExamRef exam;
    private List<SubjectScore> scores;
    private Integer totalScore;
    private Double averageScore;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentRef {
        private Integer studentId;
        private String fullName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExamRef {
        private Integer examId;
        private String examName;
        private String examDate;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubjectScore {
        private String subjectName;
        private Integer score;
        private String status;
    }
}
