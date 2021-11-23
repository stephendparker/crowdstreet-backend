package com.crowdstreet.parker.backend.controller;

import com.crowdstreet.parker.backend.thirdparty.ThirdPartyRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DataStoreExceptionController {

    Logger logger = LoggerFactory.getLogger(DataStoreExceptionController.class);

    @ExceptionHandler({DataAccessException.class})
    public ResponseEntity<Object> exception(DataAccessException exception) {

        logger.error("Error communicating with database: " + exception.getMessage(), exception);
        return new ResponseEntity<>("Unexpected data exception.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
