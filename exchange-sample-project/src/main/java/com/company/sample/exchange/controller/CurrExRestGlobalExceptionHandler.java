package com.company.sample.exchange.controller;

import com.company.sample.exchange.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;

/**
 * Exception mapping to error messages application-wide.
 */
@RestControllerAdvice
@EnableWebMvc
public class CurrExRestGlobalExceptionHandler {


    @Autowired
    private Environment env;


    @ExceptionHandler({CurrExServiceException.class, Exception.class})
    public ResponseEntity<CurrExJsonErrorBody> unDeterminedCurrExException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.undetermined");
        return new ResponseEntity<CurrExJsonErrorBody>(
                new CurrExJsonErrorBody(HttpStatus.INTERNAL_SERVER_ERROR.toString(), error, e.getClass().getName(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CurrExServiceCurrencyNotAvailableException.class)
    public ResponseEntity<CurrExJsonErrorBody> currExServiceCurrencyNotAvailableException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.currency.na");
        return new ResponseEntity<CurrExJsonErrorBody>(
                new CurrExJsonErrorBody(HttpStatus.NOT_FOUND.toString(), error, e.getClass().getName(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrExServiceDataNotFoundException.class)
    public ResponseEntity<CurrExJsonErrorBody> currExServiceDataNotFoundException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.resource.not.found");
        return new ResponseEntity<CurrExJsonErrorBody>(
                new CurrExJsonErrorBody(HttpStatus.NOT_FOUND.toString(), error, e.getClass().getName(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrExServiceCurrencyIncorrectException.class)
    public ResponseEntity<CurrExJsonErrorBody> currExServiceCurrencyIncorrectException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.currency.incorrect");
        return new ResponseEntity<CurrExJsonErrorBody>(
                new CurrExJsonErrorBody(HttpStatus.BAD_REQUEST.toString(), error, e.getClass().getName(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrExServiceDateNotRecognizedException.class)
    public ResponseEntity<CurrExJsonErrorBody> currExServiceDateNotRecognizedException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.date.incorrect");
        return new ResponseEntity<CurrExJsonErrorBody>(
                new CurrExJsonErrorBody(HttpStatus.BAD_REQUEST.toString(), error, e.getClass().getName(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrExServiceDateTooNewException.class)
    public ResponseEntity<CurrExJsonErrorBody> currExServiceDateTooNewException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.date.too.new");
        return new ResponseEntity<CurrExJsonErrorBody>(
                new CurrExJsonErrorBody(HttpStatus.NOT_FOUND.toString(), error, e.getClass().getName(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrExServiceDateTooOldException.class)
    public ResponseEntity<CurrExJsonErrorBody> currExServiceDateTooOldException(HttpServletRequest req, Exception e)
    {
        String error = env.getProperty("currex.controller.message.date.too.old");
        return new ResponseEntity<CurrExJsonErrorBody>(
                new CurrExJsonErrorBody(HttpStatus.NOT_FOUND.toString(), error, e.getClass().getName(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }


}
