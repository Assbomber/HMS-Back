package com.example.HMS.exception;


/**
 * CustomCreatedException extends base class Exception and can be thrown in any case where
 * default provided exceptions do not fulfil the exception thirst.
 * */
public class CustomCreatedException extends Exception{
    public CustomCreatedException() {
        super();
    }

    public CustomCreatedException(String message) {
        super(message);
    }



    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
