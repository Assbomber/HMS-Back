package com.example.HMS.exception;

/**
 * PatientNotFoundException extends base class Exception and is thrown in case
 * the particulars do not match with any Patient residing in the DB
 */
public class PatientNotFoundException extends Exception{
    public PatientNotFoundException() {
        super();
    }

    public PatientNotFoundException(String message) {
        super(message);
    }



    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
