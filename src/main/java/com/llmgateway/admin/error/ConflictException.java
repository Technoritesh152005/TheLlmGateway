package com.llmgateway.admin.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

//The ErrorResponseException class is a built-in Spring Framework utility class (org.springframework.web.ErrorResponseException) introduced to standardise how REST APIs send error details back to web browsers
public class ConflictException extends ErrorResponseException {

    public ConflictException(String detail){
        super(HttpStatus.CONFLICT, ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail), null);
    }
}
