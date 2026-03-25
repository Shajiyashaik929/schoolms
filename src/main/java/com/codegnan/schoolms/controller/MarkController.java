package com.codegnan.schoolms.controller;

import com.codegnan.schoolms.dto.request.MarkRequest;
import com.codegnan.schoolms.dto.request.MarkUpdateRequest;
import com.codegnan.schoolms.dto.response.ApiResponse;
import com.codegnan.schoolms.dto.response.MarkResponse;
import com.codegnan.schoolms.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService markService;

    /**
     * 4.1 GET /api/marks
     * Retrieve marks with optional query filters:
     *   ?studentId={id}  — filter by student
     *   ?subjectId={id}  — filter by subject
     *   ?examId={id}     — filter by exam
     *   ?minScore={n}    — return only marks above this threshold
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MarkResponse>>> getAllMarks(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer examId,
            @RequestParam(required = false) Integer minScore) {
        log.info("GET /api/marks — filters: studentId={}, subjectId={}, examId={}, minScore={}",
                studentId, subjectId, examId, minScore);
        List<MarkResponse> marks = markService.getAllMarks(studentId, subjectId, examId, minScore);
        return ResponseEntity.ok(ApiResponse.success(marks, marks.size()));
    }

    /**
     * 4.2 GET /api/marks/{markId}
     * Retrieve a specific mark entry by its ID.
     */
    @GetMapping("/{markId}")
    public ResponseEntity<ApiResponse<MarkResponse>> getMarkById(
            @PathVariable Integer markId) {
        log.info("GET /api/marks/{}", markId);
        return ResponseEntity.ok(ApiResponse.success(markService.getMarkById(markId)));
    }

    /**
     * 4.3 POST /api/marks
     * Enter a score for a student in a specific subject and exam.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MarkResponse>> createMark(
            @Valid @RequestBody MarkRequest request) {
        log.info("POST /api/marks");
        MarkResponse created = markService.createMark(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mark recorded successfully.", created));
    }

    /**
     * 4.4 PUT /api/marks/{markId}
     * Correct or update a previously recorded score.
     */
    @PutMapping("/{markId}")
    public ResponseEntity<ApiResponse<MarkResponse>> updateMark(
            @PathVariable Integer markId,
            @Valid @RequestBody MarkUpdateRequest request) {
        log.info("PUT /api/marks/{}", markId);
        MarkResponse updated = markService.updateMark(markId, request);
        return ResponseEntity.ok(ApiResponse.success("Mark updated successfully.", updated));
    }

    /**
     * 4.5 DELETE /api/marks/{markId}
     * Remove a mark entry from the system.
     */
    @DeleteMapping("/{markId}")
    public ResponseEntity<ApiResponse<Void>> deleteMark(
            @PathVariable Integer markId) {
        log.info("DELETE /api/marks/{}", markId);
        markService.deleteMark(markId);
        return ResponseEntity.ok(
                ApiResponse.success("Mark with ID " + markId + " has been deleted successfully."));
    }
}
