package com.spring.customer;

import com.spring.customer.exceptionhandling.GlobalExceptionHandler;
import com.spring.customer.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();


    @Test
    void testHandleRuntime() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleRuntime(new RuntimeException("Error"));

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error", response.getBody().getMessage());
    }


    @Test
    void testHandleIllegalArgument() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Invalid"));

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid", response.getBody().getMessage());
    }


    @Test
    void testHandleNotFound() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleNotFound(new jakarta.persistence.EntityNotFoundException("Not found"));

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not found", response.getBody().getMessage());
    }


    @Test
    void testHandleDataIntegrity() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleDataIntegrity(new org.springframework.dao.DataIntegrityViolationException("DB error"));

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("Duplicate"));
    }


    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("name", "String");

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleMissingParams(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("name"));
    }


    @Test
    void testHandleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "name", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiResponse<Object>> response =
                handler.handleValidationException(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("name"));
    }


    @Test
    void testHandleGlobal() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleGlobal(new Exception("Crash"));

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("Crash"));
    }
}