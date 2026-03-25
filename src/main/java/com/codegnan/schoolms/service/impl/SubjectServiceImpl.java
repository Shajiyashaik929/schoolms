package com.codegnan.schoolms.service.impl;

import com.codegnan.schoolms.constants.ErrorCodes;
import com.codegnan.schoolms.dto.request.SubjectRequest;
import com.codegnan.schoolms.dto.response.SubjectResponse;
import com.codegnan.schoolms.entity.Subject;
import com.codegnan.schoolms.exception.ResourceConflictException;
import com.codegnan.schoolms.exception.ResourceNotFoundException;
import com.codegnan.schoolms.repository.MarkRepository;
import com.codegnan.schoolms.repository.SubjectRepository;
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
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final MarkRepository markRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        log.debug("Fetching all subjects");
        return subjectRepository.findAll()
                .stream()
                .map(this::toSubjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Integer subjectId) {
        log.debug("Fetching subject with ID: {}", subjectId);
        return toSubjectResponse(findSubjectOrThrow(subjectId));
    }

    @Override
    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        log.info("Creating new subject: {}", request.getSubjectName());
        Subject subject = new Subject();
        subject.setSubjectName(request.getSubjectName().trim());
        Subject saved = subjectRepository.save(subject);
        log.info("Subject created with ID: {}", saved.getSubjectId());
        return toSubjectResponse(saved);
    }

    @Override
    @Transactional
    public SubjectResponse updateSubject(Integer subjectId, SubjectRequest request) {
        log.info("Updating subject with ID: {}", subjectId);
        Subject subject = findSubjectOrThrow(subjectId);
        subject.setSubjectName(request.getSubjectName().trim());
        Subject updated = subjectRepository.save(subject);
        log.info("Subject updated: ID={}", subjectId);
        return toSubjectResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSubject(Integer subjectId) {
        log.info("Attempting to delete subject with ID: {}", subjectId);
        Subject subject = findSubjectOrThrow(subjectId);

        long markCount = markRepository.countBySubjectSubjectId(subjectId);
        if (markCount > 0) {
            throw new ResourceConflictException(
                    ErrorCodes.SUBJECT_HAS_MARKS,
                    "Cannot delete Subject with ID " + subjectId +
                    " — it has " + markCount + " associated mark record(s). " +
                    "Remove all marks for this subject first.");
        }

        subjectRepository.delete(subject);
        log.info("Subject with ID {} deleted successfully.", subjectId);
    }

    @Override
    public Subject findSubjectOrThrow(Integer subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCodes.SUBJECT_NOT_FOUND,
                        "Subject with ID " + subjectId + " does not exist."));
    }

    // ------------------------------------------------------------------
    // Private mapping helper
    // ------------------------------------------------------------------

    private SubjectResponse toSubjectResponse(Subject subject) {
        return new SubjectResponse(subject.getSubjectId(), subject.getSubjectName());
    }
}
