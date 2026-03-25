package com.codegnan.schoolms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class SubjectRequest {

    @NotBlank(message = "Subject name is required and must not be blank.")
    private String subjectName;
}
