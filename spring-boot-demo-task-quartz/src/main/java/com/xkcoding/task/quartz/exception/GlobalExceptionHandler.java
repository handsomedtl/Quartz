package com.xkcoding.task.quartz.exception;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkcoding.task.quartz.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handle(ValidationException exception) {
    	StringBuilder sb = new StringBuilder();
    	
        if(exception instanceof ConstraintViolationException){
            ConstraintViolationException exs = (ConstraintViolationException) exception;

            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations){
            	log.debug(item.getMessage());
            	sb.append(item.getMessage() + "\n");
            }
        }
        return new ResponseEntity<>(ApiResponse.msg(sb.toString()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResponse> handle(BindException e){
    	log.error(e.getBindingResult().getFieldError().getDefaultMessage(),e);
    	
    	return new ResponseEntity<>(ApiResponse.msg(e.getBindingResult().getFieldError().getDefaultMessage()), HttpStatus.BAD_REQUEST);
    }
}
