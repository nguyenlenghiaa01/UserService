package com.example.UserService.Config;

import com.example.UserService.Exception.DuplicateEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEntity.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEntity(DuplicateEntity ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", "Duplicate Entity");
        errorBody.put("message", ex.getMessage());
        errorBody.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorBody.put("error", "Internal Server Error");
        errorBody.put("message", ex.getMessage());
        errorBody.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
}
