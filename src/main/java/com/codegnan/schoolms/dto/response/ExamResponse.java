package com.codegnan.schoolms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {

    private Integer examId;
    private String examName;
    private LocalDate examDate;
}
