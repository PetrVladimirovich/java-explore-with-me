package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import ru.practicum.ewm.dto.ApiErrorDto;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentNotValidHandler(final MethodArgumentNotValidException e) {
        log.debug(e.getMessage());
        if (Arrays.stream(e.getFieldError().getCodes()).anyMatch(c -> c.contains("EventValid"))) {
            return new ResponseEntity<>(new ApiErrorDto("BAD_REQUEST", "Incorrectly made request", e.getMessage()), HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(new ApiErrorDto("BAD_REQUEST", "Incorrectly made request", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> constraintViolationExceptionHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("CONFLICT", "Integrity constraint has been violated", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> emptyResultDataAccessHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDto> methodArgumentTypeMismatchHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("BAD_REQUEST", "Incorrectly made request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApiErrorDto> objectNotFoundHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("NOT_FOUND", "The required object not found", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintException.class)
    public ResponseEntity<ApiErrorDto> constraintExceptionHandler(final RuntimeException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("CONFLICT", "Object not available", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorDto> missingServletRequestParameterHandler(final MissingServletRequestParameterException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("BAD_REQUEST", "Incorrectly made request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> missingServletRequestParameterHandler(final IllegalArgumentException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("BAD_REQUEST", "Incorrectly made request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> overExceptionHandler(final javax.validation.ConstraintViolationException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("BAD_REQUEST", "Incorrectly made request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> dataIntegrityViolationExceptionHandler(final DataIntegrityViolationException e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("CONFLICT", "Integrity constraint has been violated", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> overExceptionHandler(final Exception e) {
        log.debug(e.getMessage());
        return new ResponseEntity<>(new ApiErrorDto("INTERNAL_SERVER_ERROR", "Unknown error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}