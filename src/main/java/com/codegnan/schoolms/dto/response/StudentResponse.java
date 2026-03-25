package com.codegnan.schoolms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Integer studentId;
    private String firstName;
    private String lastName;
    private Integer enrollmentYear;
    private String fullName;
}
