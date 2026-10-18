package com.llmgateway.admin.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ResourceNotFoundException extends ErrorResponseException {

    public ResourceNotFoundException(String detail){
        super(
                HttpStatus.CONFLICT,
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail),
                null
        );
    }
}
