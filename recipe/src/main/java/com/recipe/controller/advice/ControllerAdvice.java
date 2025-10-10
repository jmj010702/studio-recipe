package com.recipe.controller.advice;

import com.recipe.exceptions.recipe.RecipeException;
import com.recipe.exceptions.user.UserException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class ControllerAdvice {
    @ExceptionHandler(RecipeException.class)
    public ResponseEntity<Map<String, String>>RecipeEx(RecipeException ex){
        HttpStatus status = HttpStatus.resolve(ex.getCode());
        if(status == null){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Map<String ,String> errors =Map.of("message", ex.getMessage());
        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, String>>UserEx(UserException ex){
        HttpStatus status =HttpStatus.resolve(ex.getCode());
        if (status == null) {
            //log
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return ResponseEntity.status(status).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> MethodArgumentNotValidEx(MethodArgumentNotValidException ex){
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
}
