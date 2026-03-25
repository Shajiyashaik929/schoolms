package com.codegnan.schoolms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class StudentRequest {

    @NotBlank(message = "First name is required and must not be blank.")
    private String firstName;

    @NotBlank(message = "Last name is required and must not be blank.")
    private String lastName;

    @NotNull(message = "Enrollment year is required.")
    @Min(value = 1900, message = "Enrollment year must be 1900 or later.")
    @Max(value = 2100, message = "Enrollment year must be 2100 or earlier.")
    private Integer enrollmentYear;
}
