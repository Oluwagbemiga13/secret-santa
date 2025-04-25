package cz.oluwagbemiga.santa.be.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleUserRegistrationException(UserRegistrationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<ErrorResponse> handleUserLoginException(UserLoginException ex) {
        log.error("Login failed: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }


//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
//        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
//    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}