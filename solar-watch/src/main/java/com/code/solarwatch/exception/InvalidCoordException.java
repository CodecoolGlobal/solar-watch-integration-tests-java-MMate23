package com.code.solarwatch.exception;

public class InvalidCoordException extends RuntimeException {

    public InvalidCoordException() {
        super("Not valid coordinate");
    }
}
