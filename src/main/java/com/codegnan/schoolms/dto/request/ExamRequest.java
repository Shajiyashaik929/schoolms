package com.codegnan.schoolms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ExamRequest {

    @NotBlank(message = "Exam name is required and must not be blank.")
    private String examName;

    @NotNull(message = "Exam date is required.")
    private LocalDate examDate;

    // No-args constructor
    public ExamRequest() {
    }

    // Getters and Setters

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }
}