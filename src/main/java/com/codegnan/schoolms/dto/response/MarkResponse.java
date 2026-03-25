package com.codegnan.schoolms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkResponse {

    private Integer markId;
    private StudentRef student;
    private SubjectRef subject;
    private ExamRef exam;
    private Integer score;

    // -------------------------------------------------------
    // Lightweight nested reference objects (no separate files
    // needed — scoped to MarkResponse context)
    // -------------------------------------------------------

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentRef {
        private Integer studentId;
        private String fullName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectRef {
        private Integer subjectId;
        private String subjectName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExamRef {
        private Integer examId;
        private String examName;
    }
}
