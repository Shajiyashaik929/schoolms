package com.codegnan.schoolms.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SubjectRequest {

    @NotBlank(message = "Subject name is required and must not be blank.")
    private String subjectName;

    // No-args constructor
    public SubjectRequest() {
    }

    // Getter
    public String getSubjectName() {
        return subjectName;
    }

    // Setter
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}