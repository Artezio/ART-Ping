package com.artezio.artping.exception;

import com.artezio.artping.data.exceptions.*;
import com.artezio.artping.dto.ErrorDetails;
import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Обработчик ошибок
 */
@Slf4j
@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorDetails> appSecurityExceptionHandler(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> appExceptionHandler(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> validationExceptionHandler(MethodArgumentNotValidException ex,
                                                                   WebRequest request) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        StringBuilder error = new StringBuilder();
        for (ObjectError errorDetails : errors) {
            String message = errorDetails.getDefaultMessage();
            if (message != null) {
                error.append(errorDetails.getDefaultMessage()).append(" ");
            }
        }
        String errorMessage = error.toString().isEmpty() ? ex.getMessage() : error.toString();
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), errorMessage,
                request.getDescription(false));
        log.error(errorMessage);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorDetails> entityNotFoundException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CalendarHasDependenciesException.class, CalendarWithThisNameAlreadyExist.class,
            OfficeWithThisNameAlreadyExist.class, ProjectWithThisNameAlreadyExist.class, PasswordRecoveryIsNotActiveException.class})
    public ResponseEntity<ErrorDetails> calendarHasDependenciesException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OfficeHasEmployeesException.class)
    public ResponseEntity<ErrorDetails> officeHasEmployeesException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
