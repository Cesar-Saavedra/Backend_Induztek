package com.example.backend.exception;

/** Se traduce a HTTP 403 (el usuario no tiene el rol necesario). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
