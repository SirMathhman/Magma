package com.example.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorServiceTest {

    @Test
    public void testProcessInputWithEmptyString() {
        ErrorService errorService = new ErrorService();
        String result = errorService.processInput("");
        assertEquals("", result);
    }

    @Test
    public void testProcessInputWithNonEmptyStringThrowsException() {
        ErrorService errorService = new ErrorService();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            errorService.processInput("test");
        });

        assertEquals("Error processing non-empty input", exception.getMessage());
    }
}