package com.codegnan.schoolms.service.impl;

import com.codegnan.schoolms.dto.response.analytics.*;
import com.codegnan.schoolms.entity.Exam;
import com.codegnan.schoolms.entity.Mark;
import com.codegnan.schoolms.entity.Student;
import com.codegnan.schoolms.repository.MarkRepository;
import com.codegnan.schoolms.service.ExamService;
import com.codegnan.schoolms.service.ReportService;
import com.codegnan.schoolms.service.StudentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final MarkRepository markRepository;
    private final StudentService studentService;
    private final ExamService examService;

    // ================= Constructor Injection =================

    public ReportServiceImpl(MarkRepository markRepository,
                             StudentService studentService,
                             ExamService examService) {
        this.markRepository = markRepository;
        this.studentService = studentService;
        this.examService = examService;
    }

    // ------------------------------------------------------------------
    // 6.1 Leaderboard
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ExamLeaderboardResponse getExamLeaderboard(Integer examId) {
        log.debug("Building leaderboard for exam ID: {}", examId);

        Exam exam = examService.findExamOrThrow(examId);
        List<Object[]> rows = markRepository.findLeaderboardByExamId(examId);

        List<ExamLeaderboardResponse.LeaderboardEntry> leaderboard = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);

            Integer studentId = (Integer) row[0];
            String firstName  = (String) row[1];
            String lastName   = (String) row[2];
            Long totalScore   = ((Number) row[3]).longValue();
            Long subjects     = ((Number) row[4]).longValue();

            leaderboard.add(new ExamLeaderboardResponse.LeaderboardEntry(
                    i + 1,
                    new ExamLeaderboardResponse.StudentRef(studentId, firstName + " " + lastName),
                    totalScore,
                    subjects
            ));
        }

        return new ExamLeaderboardResponse(
                new ExamLeaderboardResponse.ExamRef(exam.getExamId(), exam.getExamName()),
                leaderboard
        );
    }

    // ------------------------------------------------------------------
    // 6.2 Exam Summary
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ExamSummaryResponse> getExamSummary(Integer minTotalScore) {

        int threshold = (minTotalScore != null && minTotalScore >= 0) ? minTotalScore : 0;
        log.debug("Building exam summary with minTotalScore={}", threshold);

        List<Object[]> rows = markRepository.findExamSummary(threshold);

        return rows.stream()
                .map(row -> new ExamSummaryResponse(
                        (Integer) row[0],
                        (String) row[1],
                        row[2].toString(),
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // 6.3 Compare Students
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StudentCompareResponse compareStudents(Integer studentIdA, Integer studentIdB, Integer examId) {

        log.debug("Comparing students {} and {}", studentIdA, studentIdB);

        Student studentA = studentService.findStudentOrThrow(studentIdA);
        Student studentB = studentService.findStudentOrThrow(studentIdB);

        StudentCompareResponse.StudentDetail detailA = buildStudentDetail(studentA, examId);
        StudentCompareResponse.StudentDetail detailB = buildStudentDetail(studentB, examId);

        StudentCompareResponse.Winner winner =
                determineWinner(studentA, studentB,
                        detailA.getOverallAverage(),
                        detailB.getOverallAverage());

        return new StudentCompareResponse(detailA, detailB, winner);
    }

    // ------------------------------------------------------------------
    // 6.4 Student Trend
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StudentTrendResponse getStudentTrend(Integer studentId, Integer subjectId) {

        log.debug("Building trend for student {}", studentId);

        Student student = studentService.findStudentOrThrow(studentId);
        List<Mark> marks = markRepository.findStudentTrend(studentId, subjectId);

        List<StudentTrendResponse.TrendEntry> trend = marks.stream()
                .map(m -> new StudentTrendResponse.TrendEntry(
                        m.getExam().getExamId(),
                        m.getExam().getExamName(),
                        m.getExam().getExamDate().toString(),
                        m.getScore()
                ))
                .collect(Collectors.toList());

        String direction = "Stable";
        double percent = 0.0;

        if (trend.size() >= 2) {
            int first = trend.get(0).getScore();
            int last = trend.get(trend.size() - 1).getScore();

            if (first > 0) {
                percent = round2(((last - first) * 100.0) / first);
            }

            if (last > first) direction = "Improving";
            else if (last < first) direction = "Declining";
        }

        StudentTrendResponse.SubjectRef subjectRef = null;

        if (subjectId != null && !marks.isEmpty()) {
            subjectRef = new StudentTrendResponse.SubjectRef(
                    marks.get(0).getSubject().getSubjectId(),
                    marks.get(0).getSubject().getSubjectName()
            );
        }

        return new StudentTrendResponse(
                new StudentTrendResponse.StudentRef(
                        student.getStudentId(),
                        student.getFirstName() + " " + student.getLastName()
                ),
                subjectRef,
                trend,
                direction,
                percent
        );
    }

    // ================= Helper Methods =================

    private StudentCompareResponse.StudentDetail buildStudentDetail(Student student, Integer examId) {

        List<Object[]> rows = markRepository.findSubjectAveragesForStudent(student.getStudentId(), examId);

        List<StudentCompareResponse.SubjectAverage> averages = rows.stream()
                .map(row -> new StudentCompareResponse.SubjectAverage(
                        (String) row[0],
                        round2(((Number) row[1]).doubleValue())
                ))
                .collect(Collectors.toList());

        double overall = averages.isEmpty() ? 0.0 :
                round2(averages.stream()
                        .mapToDouble(StudentCompareResponse.SubjectAverage::getAverage)
                        .average()
                        .orElse(0.0));

        return new StudentCompareResponse.StudentDetail(
                new StudentCompareResponse.StudentRef(
                        student.getStudentId(),
                        student.getFirstName() + " " + student.getLastName()
                ),
                overall,
                averages
        );
    }

    private StudentCompareResponse.Winner determineWinner(Student a, Student b,
                                                          double avgA, double avgB) {

        if (avgA >= avgB) {
            return new StudentCompareResponse.Winner(
                    a.getStudentId(),
                    a.getFirstName() + " " + a.getLastName(),
                    round2(avgA - avgB)
            );
        } else {
            return new StudentCompareResponse.Winner(
                    b.getStudentId(),
                    b.getFirstName() + " " + b.getLastName(),
                    round2(avgB - avgA)
            );
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}