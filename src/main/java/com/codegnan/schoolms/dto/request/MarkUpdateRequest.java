package com.codegnan.schoolms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class MarkUpdateRequest {

    @NotNull(message = "Score is required.")
    @Min(value = 0, message = "Score must be at least 0.")
    @Max(value = 100, message = "Score must not exceed 100.")
    private Integer score;
}
