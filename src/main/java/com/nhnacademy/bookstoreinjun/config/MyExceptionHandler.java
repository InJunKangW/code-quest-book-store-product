package com.nhnacademy.bookstoreinjun.config;


import com.nhnacademy.bookstoreinjun.dto.error.ErrorResponseDto;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.InconsistentEntityException;
import com.nhnacademy.bookstoreinjun.exception.InvalidSortNameException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundNameException;
import com.nhnacademy.bookstoreinjun.exception.NullProductException;
import com.nhnacademy.bookstoreinjun.exception.PageOutOfRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
public class MyExceptionHandler {

    private final HttpHeaders header = new HttpHeaders() {{
        setContentType(MediaType.APPLICATION_JSON);
    }};

    private ResponseEntity<ErrorResponseDto> getErrorResponse(Exception ex,HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), header, status);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponseDto> duplicateExceptionHandler(DuplicateException ex) {
        return getErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotFoundNameException.class, NotFoundIdException.class})
    public ResponseEntity<ErrorResponseDto> notFoundNameExceptionHandler(Exception ex) {
        return getErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        return getErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PageOutOfRangeException.class)
    public ResponseEntity<ErrorResponseDto> exceptionHandler(PageOutOfRangeException ex) {
        return getErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidSortNameException.class)
    public ResponseEntity<ErrorResponseDto> exceptionHandler(InvalidSortNameException ex) {
        return getErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InconsistentEntityException.class, NullProductException.class})
    public ResponseEntity<ErrorResponseDto> exceptionHandler(Exception ex) {
        return getErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}