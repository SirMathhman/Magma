package com.meti.app;

public class CompileException extends Exception {
    public CompileException(Throwable cause) {
        super(cause);
    }

    public CompileException(String message) {
        super(message);
    }
}
