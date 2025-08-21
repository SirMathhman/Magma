import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HelloWorldTest {
    @Test
    public void testEcho() {
        String input = "test string";
        String result = HelloWorld.echo(input);
        assertEquals(input, result);
    }
}
