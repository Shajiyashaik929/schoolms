package com.codegnan.schoolms.dto.response.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;

/**
 * Response DTO for GET /api/reports/students/compare
 * Side-by-side performance comparison between two students.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentCompareResponse {

    private StudentDetail studentA;
    private StudentDetail studentB;
    private Winner winner;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentDetail {
        private StudentRef student;
        private Double overallAverage;
        private List<SubjectAverage> subjectAverages;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentRef {
        private Integer studentId;
        private String fullName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubjectAverage {
        private String subjectName;
        private Double average;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Winner {
        private Integer studentId;
        private String fullName;
        private Double margin;
    }
}
