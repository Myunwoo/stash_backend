package com.ihw.stash.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.context.NoSuchMessageException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchMessageException.class)
    public ResponseEntity<String> handleNoSuchMessageException(NoSuchMessageException ex) {
        // 500 에러와 적절한 메시지 반환
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("[MSG0000] 메시지 코드를 찾을 수 없습니다.");
    }

    @ExceptionHandler(StashException.class)
    public ResponseEntity<String> handleStashException(StashException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
