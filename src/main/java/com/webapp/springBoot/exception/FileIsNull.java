package com.webapp.springBoot.exception;

public class FileIsNull extends RuntimeException {
    public FileIsNull(String message) {
        super(message);
    }
}
