package com.codegnan.schoolms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "marks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uc_marks_student_subject_exam",
            columnNames = {"student_id", "subject_id", "exam_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mark_id")
    private Integer markId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "score", nullable = false)
    private Integer score;
}
