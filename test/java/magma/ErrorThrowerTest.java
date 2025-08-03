package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ErrorThrower utility.
 */
public class ErrorThrowerTest {
    
    /**
     * Test that verifies the throwError method throws a RuntimeException for non-empty strings.
     */
    @Test
    public void testThrowErrorThrowsExceptionForNonEmptyString() {
        // Arrange
        String errorMessage = "This is a test error message";
        
        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ErrorThrower.throwError(errorMessage),
            "The throwError method should throw a RuntimeException for non-empty strings"
        );
        
        // Additional assertion to verify the error message
        assertEquals(errorMessage, exception.getMessage(), 
            "The exception message should match the provided message");
    }
    
    /**
     * Test that verifies the throwError method returns an empty string when given an empty string.
     */
    @Test
    public void testThrowErrorReturnsEmptyStringForEmptyInput() {
        // Arrange
        String emptyInput = "";
        
        // Act
        String result = ErrorThrower.throwError(emptyInput);
        
        // Assert
        assertEquals("", result, "The throwError method should return an empty string for empty input");
    }
    
    /**
     * Test that verifies the throwError method returns an empty string when given a null input.
     */
    @Test
    public void testThrowErrorReturnsEmptyStringForNullInput() {
        // Arrange
        String nullInput = null;
        
        // Act
        String result = ErrorThrower.throwError(nullInput);
        
        // Assert
        assertEquals("", result, "The throwError method should return an empty string for null input");
    }
}