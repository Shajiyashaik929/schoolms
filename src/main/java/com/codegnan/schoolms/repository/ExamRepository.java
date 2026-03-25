package com.codegnan.schoolms.repository;

import com.codegnan.schoolms.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

    /**
     * Returns all exams ordered by exam date descending (most recent first).
     * Used when the caller requests ?sort=date on GET /api/exams.
     */
    List<Exam> findAllByOrderByExamDateDesc();
}
