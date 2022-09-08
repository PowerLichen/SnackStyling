package com.snackstyling.spring.community.question.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Map;

@ControllerAdvice("com.snackstyling.spring.community.question")
public class QuestionExceptionHandler {
    @ExceptionHandler(DelQueException.class)
    public ResponseEntity<Map<String, String>> DeleteQuestion(Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("message",e.getMessage()));
    }
}
