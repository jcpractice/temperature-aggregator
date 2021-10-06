package com.qardio.temperatureaggregator.controller.error;

import com.influxdb.exceptions.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This class is acting as a centralized place for exception handling.
 */
@ControllerAdvice
@RestController
public class TemperatureServiceExceptionHandler extends ResponseEntityExceptionHandler {

    //For simplicity Exception class is being caught .Later This can be made specific to different type of exceptions.
    @ExceptionHandler(value = { Exception.class})
    public  ResponseEntity<Object> handleAllException(Exception ex, final WebRequest request) {

        final String bodyOfResponse = "Something went wrong.Please contact support";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
