package com.example.HMS.exception;

/**
 * DepartmentNotFoundException extends base class Exception and is thrown in case
 * the particulars do not match with any Department residing in the DB
 */
public class DepartmentNotFoundException extends Exception{
    public DepartmentNotFoundException() {
        super();
    }

    public DepartmentNotFoundException(String message) {
        super(message);
    }



    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
