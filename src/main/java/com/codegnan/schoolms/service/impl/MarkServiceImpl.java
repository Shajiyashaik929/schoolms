package com.codegnan.schoolms.service.impl;

import com.codegnan.schoolms.constants.ErrorCodes;
import com.codegnan.schoolms.dto.request.MarkRequest;
import com.codegnan.schoolms.dto.request.MarkUpdateRequest;
import com.codegnan.schoolms.dto.response.MarkResponse;
import com.codegnan.schoolms.entity.Exam;
import com.codegnan.schoolms.entity.Mark;
import com.codegnan.schoolms.entity.Student;
import com.codegnan.schoolms.entity.Subject;
import com.codegnan.schoolms.exception.DuplicateMarkException;
import com.codegnan.schoolms.exception.ResourceNotFoundException;
import com.codegnan.schoolms.repository.MarkRepository;
import com.codegnan.schoolms.service.ExamService;
import com.codegnan.schoolms.service.MarkService;
import com.codegnan.schoolms.service.StudentService;
import com.codegnan.schoolms.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkServiceImpl implements MarkService {

    private final MarkRepository markRepository;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final ExamService examService;

    // ------------------------------------------------------------------
    // GET /api/marks  (with optional filters)
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponse> getAllMarks(Integer studentId, Integer subjectId,
                                          Integer examId, Integer minScore) {
        log.debug("Fetching marks with filters: studentId={}, subjectId={}, examId={}, minScore={}",
                studentId, subjectId, examId, minScore);
        return markRepository.findWithFilters(studentId, subjectId, examId, minScore)
                .stream()
                .map(this::toMarkResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // GET /api/marks/{markId}
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public MarkResponse getMarkById(Integer markId) {
        log.debug("Fetching mark with ID: {}", markId);
        return toMarkResponse(findMarkOrThrow(markId));
    }

    // ------------------------------------------------------------------
    // POST /api/marks
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public MarkResponse createMark(MarkRequest request) {
        log.info("Recording new mark: studentId={}, subjectId={}, examId={}, score={}",
                request.getStudentId(), request.getSubjectId(), request.getExamId(), request.getScore());

        // Validate that all referenced entities exist
        Student student = studentService.findStudentOrThrow(request.getStudentId());
        Subject subject = subjectService.findSubjectOrThrow(request.getSubjectId());
        Exam    exam    = examService.findExamOrThrow(request.getExamId());

        // Guard against duplicate (student + subject + exam) combination
        boolean duplicate = markRepository.existsByStudentStudentIdAndSubjectSubjectIdAndExamExamId(
                request.getStudentId(), request.getSubjectId(), request.getExamId());
        if (duplicate) {
            throw new DuplicateMarkException(
                    "A mark already exists for Student " + request.getStudentId() +
                    ", Subject " + request.getSubjectId() +
                    ", Exam " + request.getExamId() + ".");
        }

        Mark mark = new Mark();
        mark.setStudent(student);
        mark.setSubject(subject);
        mark.setExam(exam);
        mark.setScore(request.getScore());

        Mark saved = markRepository.save(mark);
        log.info("Mark recorded with ID: {}", saved.getMarkId());
        return toMarkResponse(saved);
    }

    // ------------------------------------------------------------------
    // PUT /api/marks/{markId}
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public MarkResponse updateMark(Integer markId, MarkUpdateRequest request) {
        log.info("Updating mark with ID: {} — new score: {}", markId, request.getScore());
        Mark mark = findMarkOrThrow(markId);
        mark.setScore(request.getScore());
        Mark updated = markRepository.save(mark);
        log.info("Mark updated: ID={}, newScore={}", markId, request.getScore());
        return toMarkResponse(updated);
    }

    // ------------------------------------------------------------------
    // DELETE /api/marks/{markId}
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public void deleteMark(Integer markId) {
        log.info("Deleting mark with ID: {}", markId);
        Mark mark = findMarkOrThrow(markId);
        markRepository.delete(mark);
        log.info("Mark with ID {} deleted successfully.", markId);
    }

    // ------------------------------------------------------------------
    // Shared entity-lookup helper
    // ------------------------------------------------------------------

    @Override
    public Mark findMarkOrThrow(Integer markId) {
        return markRepository.findByIdWithDetails(markId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.MARK_NOT_FOUND,
                        "Mark with ID " + markId + " does not exist."));
    }

    // ------------------------------------------------------------------
    // Private mapping helper
    // ------------------------------------------------------------------

    private MarkResponse toMarkResponse(Mark mark) {
        MarkResponse.StudentRef studentRef = new MarkResponse.StudentRef(
                mark.getStudent().getStudentId(),
                mark.getStudent().getFirstName() + " " + mark.getStudent().getLastName()
        );
        MarkResponse.SubjectRef subjectRef = new MarkResponse.SubjectRef(
                mark.getSubject().getSubjectId(),
                mark.getSubject().getSubjectName()
        );
        MarkResponse.ExamRef examRef = new MarkResponse.ExamRef(
                mark.getExam().getExamId(),
                mark.getExam().getExamName()
        );
        return new MarkResponse(mark.getMarkId(), studentRef, subjectRef, examRef, mark.getScore());
    }
}
