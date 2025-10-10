package com.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle HTTP status code exceptions and return them as-is
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
        // Extract status code from the exception
        HttpStatusCode statusCode = ex.getStatusCode();

        // Extract body from the exception
        String responseBody = ex.getResponseBodyAsString();

        // Extract headers from the exception
        HttpHeaders headers = ex.getResponseHeaders() != null ? ex.getResponseHeaders() : new HttpHeaders();

        // Return the exact status, body, and headers as received from 8081
        return new ResponseEntity<>(responseBody, headers, statusCode);
    }

    // Handled by above. Kept for reference. Might be re-activated. Do Not Delete.
    /*
     * // Handle 400 Bad Request - Typically for validation errors or missing
     * parameters
     * 
     * @ExceptionHandler({HttpClientErrorException.BadRequest.class,
     * MethodArgumentNotValidException.class,
     * MissingServletRequestParameterException.class})
     * public ResponseEntity<String> handleBadRequest(Exception ex) {
     * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
     * //.body("Bad Request: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     * 
     * // Handle 401 Unauthorized - For authentication failures
     * 
     * @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
     * public ResponseEntity<String>
     * handleUnauthorized(HttpClientErrorException.Unauthorized ex) {
     * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
     * //.body("Unauthorized: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     * 
     * // Handle 403 Forbidden - When access is denied due to permissions
     * 
     * @ExceptionHandler(HttpClientErrorException.Forbidden.class)
     * public ResponseEntity<String>
     * handleForbidden(HttpClientErrorException.Forbidden ex) {
     * return ResponseEntity.status(HttpStatus.FORBIDDEN)
     * //.body("Forbidden: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     * 
     * // Handle 404 Not Found - When a requested resource is missing
     * 
     * @ExceptionHandler(HttpClientErrorException.NotFound.class)
     * public ResponseEntity<String>
     * handleNotFound(HttpClientErrorException.NotFound ex) {
     * return ResponseEntity.status(HttpStatus.NOT_FOUND)
     * //.body("Resource not found: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     * 
     * // Handle 405 Method Not Allowed - For incorrect HTTP methods (e.g., using
     * GET instead of POST)
     * 
     * @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
     * public ResponseEntity<String>
     * handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
     * return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
     * //.body("Method Not Allowed: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     * 
     * // Handle 409 Conflict - For conflicts such as duplicate resources
     * 
     * @ExceptionHandler(HttpClientErrorException.Conflict.class)
     * public ResponseEntity<String>
     * handleConflict(HttpClientErrorException.Conflict ex) {
     * return ResponseEntity.status(HttpStatus.CONFLICT)
     * //.body("Conflict: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     * 
     * // Handle 415 Unsupported Media Type - When the media type is unsupported
     * (e.g., expecting JSON but receiving XML)
     * 
     * @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
     * public ResponseEntity<String>
     * handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
     * return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
     * //.body("Unsupported Media Type: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     * 
     * // Handle 500 Internal Server Error - For any unexpected server errors
     * 
     * @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
     * public ResponseEntity<String>
     * handleInternalServerError(HttpServerErrorException.InternalServerError ex) {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * //.body("Internal server error: " + ex.getMessage());
     * .body(ex.getMessage());
     * }
     */
    // Handle any other exceptions - Fallback for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                // .body("An unexpected error occurred: " + ex.getMessage());
                .body(ex.getMessage());
    }

}
