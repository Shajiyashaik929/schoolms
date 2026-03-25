package com.codegnan.schoolms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "exams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "exam_name", nullable = false, length = 50)
    private String examName;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;
}
