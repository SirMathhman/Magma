package com.example.magma;

public class ErrorService {

    public String processInput(String input) {
        if (input.isEmpty()) {
            return "";
        } else {
            throw new RuntimeException("Error processing non-empty input");
        }
    }
}