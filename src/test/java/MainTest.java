import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Main class.
 */
public class MainTest {
    
    /**
     * Test the add method in the Main class.
     */
    @Test
    public void testAdd() {
        // Test case 1: Adding two positive numbers
        assertEquals(8, Main.add(5, 3));
        
        // Test case 2: Adding a positive and a negative number
        assertEquals(2, Main.add(5, -3));
        
        // Test case 3: Adding two negative numbers
        assertEquals(-8, Main.add(-5, -3));
        
        // Test case 4: Adding zero
        assertEquals(5, Main.add(5, 0));
    }
    
    /**
     * Test the processString method in the Main class.
     */
    @Test
    public void testProcessString() {
        // Test case 1: Regular string
        assertEquals("hello", Main.processString("hello"));
        
        // Test case 2: Empty string
        assertEquals("", Main.processString(""));
        
        // Test case 3: String with special characters
        assertEquals("Hello, World!", Main.processString("Hello, World!"));
    }
}