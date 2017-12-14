package com.agrawal93.kafka.manager.exception.handler;

import com.agrawal93.kafka.manager.exception.AlreadyExistsException;
import com.agrawal93.kafka.manager.exception.InvalidPayloadException;
import com.agrawal93.kafka.manager.exception.NotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@ControllerAdvice
public class KafkaManagerExceptionHandler {

    @ExceptionHandler(InvalidPayloadException.class)
    public void invalidPayloadExceptions(HttpServletResponse response, InvalidPayloadException exception) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public void notFoundExceptions(HttpServletResponse response, NotFoundException exception) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public void alreadyExistsExceptions(HttpServletResponse response, AlreadyExistsException exception) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void anyExceptions(HttpServletResponse response, Exception exception) throws IOException {
        exception.printStackTrace();
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

}
