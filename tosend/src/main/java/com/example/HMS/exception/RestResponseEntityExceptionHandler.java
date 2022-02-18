package com.example.HMS.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler and acts as a ControllerAdvice
 * that intercepts Custom exceptions defined below when thrown and generates a response back to the client.
 * */
@ControllerAdvice
@ResponseStatus
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Intercepts IllegalArgumentException and CustomCreatedException
     * */
    @ExceptionHandler({IllegalArgumentException.class,CustomCreatedException.class})
    public ResponseEntity<FailureMessage> illegalArgumentException(Exception exception){
        FailureMessage errorMessage=new FailureMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    /**
     * Intercepts StaffNotFoundException and DepartmentNotFoundException
     * */
    @ExceptionHandler({AppointmentNotFoundException.class,PatientNotFoundException.class,StaffNotFoundException.class,DepartmentNotFoundException.class})
    public ResponseEntity<FailureMessage> staffNotFoundException(Exception exception){
        FailureMessage errorMessage=new FailureMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    /**
     * Intercepts MethodArgumentNotValidException
     * */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return ResponseEntity.badRequest().body(new FailureMessage(HttpStatus.BAD_REQUEST, fieldErrors.get(0).getDefaultMessage()));

    }


}
