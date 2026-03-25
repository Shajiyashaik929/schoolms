package com.codegnan.schoolms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Standard error response envelope returned by the GlobalExceptionHandler
 * for all error scenarios (4xx, 5xx).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String status = "error";
    private String errorCode;
    private String message;
    private String timestamp;
    private String path;

    public ErrorResponse(String errorCode, String message, String timestamp, String path) {
        this.status = "error";
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
}
