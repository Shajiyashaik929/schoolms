package com.codegnan.schoolms.repository;

import com.codegnan.schoolms.entity.Mark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Integer> {

    // ------------------------------------------------------------------
    // Single-record lookups with eager joins (avoids N+1 queries)
    // ------------------------------------------------------------------

    @Query("SELECT m FROM Mark m JOIN FETCH m.student JOIN FETCH m.subject JOIN FETCH m.exam " +
           "WHERE m.markId = :id")
    Optional<Mark> findByIdWithDetails(@Param("id") Integer id);

    // ------------------------------------------------------------------
    // Filtered list query for GET /api/marks
    // All parameters are optional; NULL means "no filter on this field".
    // ------------------------------------------------------------------

    @Query("SELECT m FROM Mark m JOIN FETCH m.student JOIN FETCH m.subject JOIN FETCH m.exam " +
           "WHERE (:studentId IS NULL OR m.student.studentId = :studentId) " +
           "AND (:subjectId IS NULL OR m.subject.subjectId = :subjectId) " +
           "AND (:examId IS NULL OR m.exam.examId = :examId) " +
           "AND (:minScore IS NULL OR m.score >= :minScore)")
    List<Mark> findWithFilters(@Param("studentId") Integer studentId,
                               @Param("subjectId") Integer subjectId,
                               @Param("examId") Integer examId,
                               @Param("minScore") Integer minScore);

    // ------------------------------------------------------------------
    // Duplicate-check before recording a new mark
    // ------------------------------------------------------------------

    boolean existsByStudentStudentIdAndSubjectSubjectIdAndExamExamId(
            Integer studentId, Integer subjectId, Integer examId);

    // ------------------------------------------------------------------
    // Referential integrity counts (used before deleting parent records)
    // ------------------------------------------------------------------

    long countByStudentStudentId(Integer studentId);

    long countBySubjectSubjectId(Integer subjectId);

    long countByExamExamId(Integer examId);

    // ------------------------------------------------------------------
    // Analytics: marks for a student (all exams, eager-loaded)
    // ------------------------------------------------------------------

    @Query("SELECT m FROM Mark m JOIN FETCH m.subject JOIN FETCH m.exam " +
           "WHERE m.student.studentId = :studentId " +
           "ORDER BY m.exam.examDate ASC, m.subject.subjectName ASC")
    List<Mark> findByStudentIdWithDetails(@Param("studentId") Integer studentId);

    // ------------------------------------------------------------------
    // Analytics: marks for a student in a specific exam
    // ------------------------------------------------------------------

    @Query("SELECT m FROM Mark m JOIN FETCH m.subject " +
           "WHERE m.student.studentId = :studentId AND m.exam.examId = :examId")
    List<Mark> findByStudentIdAndExamId(@Param("studentId") Integer studentId,
                                        @Param("examId") Integer examId);

    // ------------------------------------------------------------------
    // Analytics: all marks for a specific exam (for pass/fail and leaderboard)
    // ------------------------------------------------------------------

    @Query("SELECT m FROM Mark m JOIN FETCH m.student JOIN FETCH m.subject " +
           "WHERE m.exam.examId = :examId " +
           "ORDER BY m.student.studentId ASC")
    List<Mark> findByExamIdWithDetails(@Param("examId") Integer examId);

    // ------------------------------------------------------------------
    // Analytics: subject-level aggregates across all exams
    // Returns: [subjectId, subjectName, avgScore, count, maxScore, minScore]
    // ------------------------------------------------------------------

    @Query("SELECT m.subject.subjectId, m.subject.subjectName, " +
           "AVG(m.score), COUNT(m), MAX(m.score), MIN(m.score) " +
           "FROM Mark m " +
           "GROUP BY m.subject.subjectId, m.subject.subjectName " +
           "ORDER BY m.subject.subjectName ASC")
    List<Object[]> findSubjectAggregates();

    // ------------------------------------------------------------------
    // Analytics: top performers — top N marks by score, optionally scoped to an exam
    // Returns Mark entities ordered by score DESC (Pageable limits the result)
    // ------------------------------------------------------------------

    @Query("SELECT m FROM Mark m JOIN FETCH m.student JOIN FETCH m.subject JOIN FETCH m.exam " +
           "WHERE (:examId IS NULL OR m.exam.examId = :examId) " +
           "ORDER BY m.score DESC")
    List<Mark> findTopPerformers(@Param("examId") Integer examId, Pageable pageable);

    // ------------------------------------------------------------------
    // Analytics: exam leaderboard — students ranked by total score in one exam
    // Returns: [studentId, firstName, lastName, totalScore, subjectsAttempted]
    // ------------------------------------------------------------------

    @Query("SELECT m.student.studentId, m.student.firstName, m.student.lastName, " +
           "SUM(m.score), COUNT(m) " +
           "FROM Mark m " +
           "WHERE m.exam.examId = :examId " +
           "GROUP BY m.student.studentId, m.student.firstName, m.student.lastName " +
           "ORDER BY SUM(m.score) DESC")
    List<Object[]> findLeaderboardByExamId(@Param("examId") Integer examId);

    // ------------------------------------------------------------------
    // Analytics: exam summary — total score per exam, filtered by minimum total
    // Returns: [examId, examName, examDate, totalScoreSum, studentsAppeared]
    // ------------------------------------------------------------------

    @Query("SELECT m.exam.examId, m.exam.examName, m.exam.examDate, " +
           "SUM(m.score), COUNT(DISTINCT m.student.studentId) " +
           "FROM Mark m " +
           "GROUP BY m.exam.examId, m.exam.examName, m.exam.examDate " +
           "HAVING SUM(m.score) >= :minTotalScore " +
           "ORDER BY SUM(m.score) DESC")
    List<Object[]> findExamSummary(@Param("minTotalScore") Integer minTotalScore);

    // ------------------------------------------------------------------
    // Analytics: per-subject averages for a student (used in comparison)
    // Returns: [subjectName, avgScore]
    // ------------------------------------------------------------------

    @Query("SELECT m.subject.subjectName, AVG(m.score) " +
           "FROM Mark m " +
           "WHERE m.student.studentId = :studentId " +
           "AND (:examId IS NULL OR m.exam.examId = :examId) " +
           "GROUP BY m.subject.subjectId, m.subject.subjectName " +
           "ORDER BY m.subject.subjectName ASC")
    List<Object[]> findSubjectAveragesForStudent(@Param("studentId") Integer studentId,
                                                  @Param("examId") Integer examId);

    // ------------------------------------------------------------------
    // Analytics: score trend for a student across exams, optionally by subject
    // ------------------------------------------------------------------

    @Query("SELECT m FROM Mark m JOIN FETCH m.exam JOIN FETCH m.subject " +
           "WHERE m.student.studentId = :studentId " +
           "AND (:subjectId IS NULL OR m.subject.subjectId = :subjectId) " +
           "ORDER BY m.exam.examDate ASC")
    List<Mark> findStudentTrend(@Param("studentId") Integer studentId,
                                @Param("subjectId") Integer subjectId);
}
