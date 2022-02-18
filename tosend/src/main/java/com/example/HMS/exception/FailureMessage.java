package com.example.HMS.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Failure Message is class that just provides body/structure for outgoing failure responses
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailureMessage {
    private HttpStatus status;
    private String message;
}
