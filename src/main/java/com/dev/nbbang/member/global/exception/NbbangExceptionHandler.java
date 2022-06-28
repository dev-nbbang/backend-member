package com.dev.nbbang.member.global.exception;

import com.dev.nbbang.member.global.dto.response.CommonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.ResultSet;

@RestControllerAdvice
@Slf4j
public class NbbangExceptionHandler {
    @ExceptionHandler(NbbangCommonException.class)
    public ResponseEntity<CommonResponse> handleBaseException(NbbangCommonException e) {
        log.warn("Nbbang Exception Code : " + e.getErrorCode());
        log.warn("Nbbang Exception message : " + e.getMessage());

        return ResponseEntity.ok(CommonResponse.response(false, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgument Exception");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.response(false, "Bad Request"));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<CommonResponse> handleJsonProcessingException(JsonProcessingException e) {
        log.warn("JsonProcessing Exception");

        return ResponseEntity.ok(CommonResponse.response(false, "Kafka Message Send Exception"));
    }
}
