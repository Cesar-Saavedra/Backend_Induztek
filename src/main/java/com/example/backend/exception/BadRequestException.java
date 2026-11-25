package com.example.backend.exception;

/** Se traduce a HTTP 400 (datos que no cumplen una regla del negocio). */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
