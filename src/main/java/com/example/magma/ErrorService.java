package com.example.magma;

public class ErrorService {

    public void alwaysError() {
        throw new RuntimeException("This method always throws an error");
    }
}