package cz.oluwagbemiga.santa.be.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleInvalidRequestException() {
        String message = "Invalid request message";
        InvalidRequestException exception = new InvalidRequestException(message);

        ResponseEntity<ErrorResponse> response = handler.handleInvalidRequestException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals(message, response.getBody().message());
    }

    @Test
    void handleUserRegistrationException() {
        String message = "Registration failed";
        UserRegistrationException exception = new UserRegistrationException(message);

        ResponseEntity<ErrorResponse> response = handler.handleUserRegistrationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals(message, response.getBody().message());
    }

    @Test
    void handleUserLoginException() {
        String message = "Login failed";
        UserLoginException exception = new UserLoginException(message);

        ResponseEntity<ErrorResponse> response = handler.handleUserLoginException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().status());
        assertEquals(message, response.getBody().message());
    }

    @Test
    void handleResourceNotFoundException() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals(message, response.getBody().message());
    }

    @Test
    void handleUnauthorizedAccessException() {
        String message = "Unauthorized access";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(message);

        ResponseEntity<ErrorResponse> response = handler.handleUnauthorizedAccessException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().status());
        assertEquals(message, response.getBody().message());
    }
}