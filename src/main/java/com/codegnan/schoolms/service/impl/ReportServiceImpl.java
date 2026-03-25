package com.codegnan.schoolms.service.impl;

import com.codegnan.schoolms.dto.response.analytics.*;
import com.codegnan.schoolms.entity.Exam;
import com.codegnan.schoolms.entity.Mark;
import com.codegnan.schoolms.entity.Student;
import com.codegnan.schoolms.repository.MarkRepository;
import com.codegnan.schoolms.service.ExamService;
import com.codegnan.schoolms.service.ReportService;
import com.codegnan.schoolms.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MarkRepository markRepository;
    private final StudentService studentService;
    private final ExamService examService;

    // ------------------------------------------------------------------
    // 6.1  GET /api/reports/exams/{examId}/leaderboard
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
            Integer studentId         = (Integer) row[0];
            String  firstName         = (String)  row[1];
            String  lastName          = (String)  row[2];
            Long    totalScore        = ((Number)  row[3]).longValue();
            Long    subjectsAttempted = ((Number)  row[4]).longValue();

            leaderboard.add(new ExamLeaderboardResponse.LeaderboardEntry(
                    i + 1,
                    new ExamLeaderboardResponse.StudentRef(studentId, firstName + " " + lastName),
                    totalScore,
                    subjectsAttempted
            ));
        }

        return new ExamLeaderboardResponse(
                new ExamLeaderboardResponse.ExamRef(exam.getExamId(), exam.getExamName()),
                leaderboard
        );
    }

    // ------------------------------------------------------------------
    // 6.2  GET /api/reports/exams/summary
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<ExamSummaryResponse> getExamSummary(Integer minTotalScore) {
        int threshold = (minTotalScore != null && minTotalScore >= 0) ? minTotalScore : 0;
        log.debug("Building exam summary with minTotalScore={}", threshold);

        List<Object[]> rows = markRepository.findExamSummary(threshold);

        return rows.stream().map(row -> new ExamSummaryResponse(
                (Integer) row[0],             // examId
                (String)  row[1],             // examName
                row[2].toString(),            // examDate (LocalDate → String)
                ((Number) row[3]).longValue(), // totalScoreSum
                ((Number) row[4]).longValue()  // studentsAppeared
        )).collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // 6.3  GET /api/reports/students/compare
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StudentCompareResponse compareStudents(Integer studentIdA, Integer studentIdB,
                                                  Integer examId) {
        log.debug("Comparing student ID={} vs ID={}, examId={}", studentIdA, studentIdB, examId);

        Student studentA = studentService.findStudentOrThrow(studentIdA);
        Student studentB = studentService.findStudentOrThrow(studentIdB);

        StudentCompareResponse.StudentDetail detailA = buildStudentDetail(studentA, examId);
        StudentCompareResponse.StudentDetail detailB = buildStudentDetail(studentB, examId);

        StudentCompareResponse.Winner winner = determineWinner(
                studentA, studentB, detailA.getOverallAverage(), detailB.getOverallAverage());

        return new StudentCompareResponse(detailA, detailB, winner);
    }

    // ------------------------------------------------------------------
    // 6.4  GET /api/reports/students/{studentId}/trend
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StudentTrendResponse getStudentTrend(Integer studentId, Integer subjectId) {
        log.debug("Building score trend for student ID={}, subjectId={}", studentId, subjectId);

        Student    student = studentService.findStudentOrThrow(studentId);
        List<Mark> marks   = markRepository.findStudentTrend(studentId, subjectId);

        List<StudentTrendResponse.TrendEntry> trendEntries = marks.stream()
                .map(m -> new StudentTrendResponse.TrendEntry(
                        m.getExam().getExamId(),
                        m.getExam().getExamName(),
                        m.getExam().getExamDate().toString(),
                        m.getScore()))
                .collect(Collectors.toList());

        String trendDirection = "Stable";
        double changePercent  = 0.0;

        if (trendEntries.size() >= 2) {
            int firstScore = trendEntries.get(0).getScore();
            int lastScore  = trendEntries.get(trendEntries.size() - 1).getScore();
            if (firstScore > 0) {
                changePercent = round2(((lastScore - firstScore) * 100.0) / firstScore);
            }
            if      (lastScore > firstScore) trendDirection = "Improving";
            else if (lastScore < firstScore) trendDirection = "Declining";
        }

        // Null when not scoped to a specific subject — @JsonInclude(NON_NULL) suppresses the field
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
                        student.getFirstName() + " " + student.getLastName()),
                subjectRef,
                trendEntries,
                trendDirection,
                changePercent
        );
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private StudentCompareResponse.StudentDetail buildStudentDetail(Student student, Integer examId) {
        List<Object[]> rows = markRepository.findSubjectAveragesForStudent(
                student.getStudentId(), examId);

        List<StudentCompareResponse.SubjectAverage> subjectAverages = rows.stream()
                .map(row -> new StudentCompareResponse.SubjectAverage(
                        (String) row[0],
                        round2(((Number) row[1]).doubleValue())))
                .collect(Collectors.toList());

        double overallAverage = subjectAverages.isEmpty() ? 0.0 :
                round2(subjectAverages.stream()
                        .mapToDouble(StudentCompareResponse.SubjectAverage::getAverage)
                        .average()
                        .orElse(0.0));

        return new StudentCompareResponse.StudentDetail(
                new StudentCompareResponse.StudentRef(
                        student.getStudentId(),
                        student.getFirstName() + " " + student.getLastName()),
                overallAverage,
                subjectAverages
        );
    }

    private StudentCompareResponse.Winner determineWinner(Student studentA, Student studentB,
                                                          double avgA, double avgB) {
        if (avgA >= avgB) {
            return new StudentCompareResponse.Winner(
                    studentA.getStudentId(),
                    studentA.getFirstName() + " " + studentA.getLastName(),
                    round2(avgA - avgB));
        } else {
            return new StudentCompareResponse.Winner(
                    studentB.getStudentId(),
                    studentB.getFirstName() + " " + studentB.getLastName(),
                    round2(avgB - avgA));
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
