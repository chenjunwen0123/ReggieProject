package com.example.reggie.common.handler;

import com.example.reggie.common.CustomException;
import com.example.reggie.common.Res;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Res<String> exceptionHandler(Exception e){
        log.error(e.getMessage());
        if(e instanceof DuplicateKeyException){
            return Res.error("Error:Duplicate Username!");
        }
        if(e instanceof CustomException){
            return Res.error(e.getMessage());
        }
        return Res.error("Unknown exception") ;
    }

}
