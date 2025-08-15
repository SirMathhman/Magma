package com.example.magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorServiceTest {
    
    @Test
    public void testAlwaysErrorThrowsException() {
        ErrorService errorService = new ErrorService();
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            errorService.alwaysError();
        });
        
        assertEquals("This method always throws an error", exception.getMessage());
    }
}