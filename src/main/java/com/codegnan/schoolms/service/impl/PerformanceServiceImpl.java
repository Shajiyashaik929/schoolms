package com.codegnan.schoolms.service.impl;

import com.codegnan.schoolms.dto.response.analytics.*;
import com.codegnan.schoolms.entity.Exam;
import com.codegnan.schoolms.entity.Mark;
import com.codegnan.schoolms.entity.Student;
import com.codegnan.schoolms.repository.MarkRepository;
import com.codegnan.schoolms.service.ExamService;
import com.codegnan.schoolms.service.PerformanceService;
import com.codegnan.schoolms.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    private static final int DEFAULT_PASS_THRESHOLD = 70;

    private final MarkRepository markRepository;
    private final StudentService studentService;
    private final ExamService examService;

    // ------------------------------------------------------------------
    // 5.1  GET /api/performance/students/{studentId}
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StudentFullPerformanceResponse getFullPerformanceReport(Integer studentId) {
        log.debug("Building full performance report for student ID: {}", studentId);

        Student student = studentService.findStudentOrThrow(studentId);
        List<Mark> marks = markRepository.findByStudentIdWithDetails(studentId);

        // Group marks by exam (preserving insertion order)
        Map<Integer, List<Mark>> marksByExam = new LinkedHashMap<>();
        for (Mark mark : marks) {
            marksByExam.computeIfAbsent(mark.getExam().getExamId(), k -> new ArrayList<>()).add(mark);
        }

        List<StudentFullPerformanceResponse.ExamPerformance> examPerformances = new ArrayList<>();
        int totalScore = 0;
        int totalCount = 0;

        for (Map.Entry<Integer, List<Mark>> entry : marksByExam.entrySet()) {
            List<Mark> examMarks = entry.getValue();
            Exam exam = examMarks.get(0).getExam();

            List<StudentFullPerformanceResponse.SubjectScore> subjectScores = examMarks.stream()
                    .map(m -> new StudentFullPerformanceResponse.SubjectScore(
                            m.getSubject().getSubjectName(),
                            m.getScore(),
                            m.getScore() >= DEFAULT_PASS_THRESHOLD ? "Pass" : "Fail"))
                    .collect(Collectors.toList());

            int examTotal = examMarks.stream().mapToInt(Mark::getScore).sum();
            double examAverage = round2((double) examTotal / examMarks.size());

            totalScore += examTotal;
            totalCount += examMarks.size();

            examPerformances.add(new StudentFullPerformanceResponse.ExamPerformance(
                    exam.getExamId(),
                    exam.getExamName(),
                    exam.getExamDate().toString(),
                    subjectScores,
                    examTotal,
                    examAverage
            ));
        }

        double overallAverage = totalCount > 0 ? round2((double) totalScore / totalCount) : 0.0;

        return new StudentFullPerformanceResponse(
                new StudentFullPerformanceResponse.StudentDetail(
                        student.getStudentId(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getEnrollmentYear()),
                overallAverage,
                marksByExam.size(),
                examPerformances
        );
    }

    // ------------------------------------------------------------------
    // 5.2  GET /api/performance/students/{studentId}/exams/{examId}
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public StudentExamScoresResponse getStudentPerformanceInExam(Integer studentId, Integer examId) {
        log.debug("Fetching student ID={} performance in exam ID={}", studentId, examId);

        Student student = studentService.findStudentOrThrow(studentId);
        Exam    exam    = examService.findExamOrThrow(examId);

        List<Mark> marks = markRepository.findByStudentIdAndExamId(studentId, examId);

        List<StudentExamScoresResponse.SubjectScore> scores = marks.stream()
                .map(m -> new StudentExamScoresResponse.SubjectScore(
                        m.getSubject().getSubjectName(),
                        m.getScore(),
                        m.getScore() >= DEFAULT_PASS_THRESHOLD ? "Pass" : "Fail"))
                .collect(Collectors.toList());

        int totalScore   = marks.stream().mapToInt(Mark::getScore).sum();
        double avgScore  = marks.isEmpty() ? 0.0 : round2((double) totalScore / marks.size());

        return new StudentExamScoresResponse(
                new StudentExamScoresResponse.StudentRef(
                        student.getStudentId(),
                        student.getFirstName() + " " + student.getLastName()),
                new StudentExamScoresResponse.ExamRef(
                        exam.getExamId(), exam.getExamName(), exam.getExamDate().toString()),
                scores,
                totalScore,
                avgScore
        );
    }

    // ------------------------------------------------------------------
    // 5.3  GET /api/performance/subjects/averages
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<SubjectAverageResponse> getSubjectAverages() {
        log.debug("Fetching subject-wise aggregates across all exams");
        List<Object[]> rows = markRepository.findSubjectAggregates();

        return rows.stream().map(row -> new SubjectAverageResponse(
                (Integer) row[0],                        // subjectId
                (String)  row[1],                        // subjectName
                round2(((Number) row[2]).doubleValue()), // averageScore
                ((Number) row[3]).longValue(),            // totalMarksRecorded
                ((Number) row[4]).intValue(),             // highestScore
                ((Number) row[5]).intValue()              // lowestScore
        )).collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // 5.4  GET /api/performance/top-performers
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<TopPerformerResponse> getTopPerformers(Integer limit, Integer examId) {
        int effectiveLimit = (limit != null && limit > 0) ? limit : 5;
        log.debug("Fetching top {} performers, examId={}", effectiveLimit, examId);

        List<Mark> marks = markRepository.findTopPerformers(examId, PageRequest.of(0, effectiveLimit));

        List<TopPerformerResponse> result = new ArrayList<>();
        for (int i = 0; i < marks.size(); i++) {
            Mark m = marks.get(i);
            result.add(new TopPerformerResponse(
                    i + 1,
                    new TopPerformerResponse.StudentRef(
                            m.getStudent().getStudentId(),
                            m.getStudent().getFirstName() + " " + m.getStudent().getLastName()),
                    new TopPerformerResponse.ExamRef(m.getExam().getExamName()),
                    new TopPerformerResponse.SubjectRef(m.getSubject().getSubjectName()),
                    m.getScore()
            ));
        }
        return result;
    }

    // ------------------------------------------------------------------
    // 5.5  GET /api/performance/exams/{examId}/pass-fail
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public PassFailReportResponse getPassFailReport(Integer examId, Integer passThreshold) {
        int threshold = (passThreshold != null && passThreshold >= 0)
                ? passThreshold : DEFAULT_PASS_THRESHOLD;
        log.debug("Building pass/fail report for exam ID={}, threshold={}", examId, threshold);

        Exam       exam  = examService.findExamOrThrow(examId);
        List<Mark> marks = markRepository.findByExamIdWithDetails(examId);

        // Group by student
        Map<Integer, List<Mark>> marksByStudent = new LinkedHashMap<>();
        for (Mark mark : marks) {
            marksByStudent.computeIfAbsent(
                    mark.getStudent().getStudentId(), k -> new ArrayList<>()).add(mark);
        }

        List<PassFailReportResponse.StudentResult> results = new ArrayList<>();
        int passedCount = 0;

        for (Map.Entry<Integer, List<Mark>> entry : marksByStudent.entrySet()) {
            List<Mark> studentMarks = entry.getValue();
            Student student = studentMarks.get(0).getStudent();

            List<PassFailReportResponse.SubjectResult> subjectResults = studentMarks.stream()
                    .map(m -> new PassFailReportResponse.SubjectResult(
                            m.getSubject().getSubjectName(),
                            m.getScore(),
                            m.getScore() >= threshold ? "Pass" : "Fail"))
                    .collect(Collectors.toList());

            boolean allPassed = subjectResults.stream()
                    .allMatch(sr -> "Pass".equals(sr.getStatus()));
            if (allPassed) passedCount++;

            results.add(new PassFailReportResponse.StudentResult(
                    new PassFailReportResponse.StudentRef(
                            student.getStudentId(),
                            student.getFirstName() + " " + student.getLastName()),
                    subjectResults,
                    allPassed ? "Pass" : "Fail"
            ));
        }

        int totalStudents = results.size();
        int failedCount   = totalStudents - passedCount;
        double passPercentage = totalStudents > 0
                ? round2((passedCount * 100.0) / totalStudents) : 0.0;

        return new PassFailReportResponse(
                new PassFailReportResponse.ExamRef(
                        exam.getExamId(), exam.getExamName(), exam.getExamDate().toString()),
                threshold,
                new PassFailReportResponse.Summary(totalStudents, passedCount, failedCount, passPercentage),
                results
        );
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
