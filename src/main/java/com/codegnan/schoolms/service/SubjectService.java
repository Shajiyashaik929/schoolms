package com.codegnan.schoolms.service;

import com.codegnan.schoolms.dto.request.SubjectRequest;
import com.codegnan.schoolms.dto.response.SubjectResponse;
import com.codegnan.schoolms.entity.Subject;

import java.util.List;

/**
 * Contract for subject CRUD operations.
 * Implemented by {@link com.codegnan.schoolms.service.impl.SubjectServiceImpl}.
 */
public interface SubjectService {

    /**
     * Returns all subjects in the system.
     *
     * @return list of subject response DTOs
     */
    List<SubjectResponse> getAllSubjects();

    /**
     * Retrieves a single subject by primary key.
     *
     * @param subjectId the subject's ID
     * @return subject response DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if not found
     */
    SubjectResponse getSubjectById(Integer subjectId);

    /**
     * Creates and persists a new subject record.
     *
     * @param request validated inbound payload
     * @return the created subject as a response DTO
     */
    SubjectResponse createSubject(SubjectRequest request);

    /**
     * Updates an existing subject record.
     *
     * @param subjectId the subject's ID
     * @param request   validated inbound payload
     * @return the updated subject as a response DTO
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if not found
     */
    SubjectResponse updateSubject(Integer subjectId, SubjectRequest request);

    /**
     * Deletes a subject record.
     *
     * @param subjectId the subject's ID
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException  if not found
     * @throws com.codegnan.schoolms.exception.ResourceConflictException if the subject has associated marks
     */
    void deleteSubject(Integer subjectId);

    /**
     * Retrieves the {@link Subject} JPA entity or throws if absent.
     * Shared with MarkService.
     *
     * @param subjectId the subject's ID
     * @return the found entity
     * @throws com.codegnan.schoolms.exception.ResourceNotFoundException if not found
     */
    Subject findSubjectOrThrow(Integer subjectId);
}
