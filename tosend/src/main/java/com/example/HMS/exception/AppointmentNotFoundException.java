package com.example.HMS.exception;

/**
 * AppointmentNotFoundException extends base class Exception and must be thrown in case
 * the particulars are not found in the DB.
 * */
public class AppointmentNotFoundException extends Exception{
    public AppointmentNotFoundException() {
        super();
    }

    public AppointmentNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
