package com.codegnan.schoolms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ExamRequest {

    @NotBlank(message = "Exam name is required and must not be blank.")
    private String examName;

    @NotNull(message = "Exam date is required.")
    private LocalDate examDate;
}
