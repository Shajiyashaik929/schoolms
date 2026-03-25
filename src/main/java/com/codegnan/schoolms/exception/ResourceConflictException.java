package com.codegnan.schoolms.exception;

/**
 * Thrown when a delete operation would violate referential integrity
 * (e.g., deleting a Student or Exam that has associated Marks records).
 * Mapped to HTTP 409 Conflict by the GlobalExceptionHandler.
 */
public class ResourceConflictException extends RuntimeException {

    private final String errorCode;

    public ResourceConflictException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
