package com.crowdstreet.parker.backend.controller;

import com.crowdstreet.parker.backend.thirdparty.ThirdPartyRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ThirdPartyExceptionController extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ThirdPartyExceptionController.class);

    @ExceptionHandler({ThirdPartyRequestException.class})
    public ResponseEntity<Object> exception(ThirdPartyRequestException exception) {

        logger.error("Error communicating with third party: " + exception.getMessage(), exception);
        return new ResponseEntity<>("Unexpected error communicating with third party.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
