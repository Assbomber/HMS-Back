package com.example.HMS.exception;

/**
 * StaffNotFoundException extends base class Exception
 * and is thrown in case Staff with particular
 * data is not found in DB
 * */
public class StaffNotFoundException extends Exception{
    public StaffNotFoundException() {
        super();
    }

    public StaffNotFoundException(String message) {
        super(message);
    }


    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
