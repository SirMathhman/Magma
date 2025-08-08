package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {
    
    @Test
    public void shouldReturnSameString() {
        // Arrange
        String input = "hello";
        
        // Act
        String result = StringUtils.echo(input);
        
        // Assert
        assertEquals(input, result, "The function should return the same string that was provided");
    }
}