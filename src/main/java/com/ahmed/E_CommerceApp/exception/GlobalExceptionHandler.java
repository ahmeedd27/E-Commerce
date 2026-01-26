package com.ahmed.E_CommerceApp.exception;

import com.ahmed.E_CommerceApp.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex , WebRequest request){
        ErrorDetails errorDetails=new
                ErrorDetails(new Date() , ex.getMessage() , request.getDescription(false));
        return new ResponseEntity<>(errorDetails , HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStockException(InsufficientStockException ex , WebRequest request){
        ErrorDetails errorDetails=new
                ErrorDetails(new Date() , ex.getMessage() , request.getDescription(false));
        return new ResponseEntity<>(errorDetails , HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex , WebRequest request){
        ErrorDetails errorDetails=new
                ErrorDetails(new Date() , ex.getMessage() , request.getDescription(false));
        return new ResponseEntity<>(errorDetails , HttpStatus.NOT_FOUND);
    }
}
