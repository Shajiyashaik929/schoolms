package com.codegnan.schoolms.dto.response.analytics;

import lombok.*;
import java.util.List;

/**
 * Response DTO for GET /api/reports/exams/{examId}/leaderboard
 * Students ranked by total score in a specific exam.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamLeaderboardResponse {

    private ExamRef exam;
    private List<LeaderboardEntry> leaderboard;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExamRef {
        private Integer examId;
        private String examName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LeaderboardEntry {
        private Integer rank;
        private StudentRef student;
        private Long totalScore;
        private Long subjectsAttempted;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentRef {
        private Integer studentId;
        private String fullName;
    }
}
