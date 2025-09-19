package magma;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AppTest {
    @Test
    public void testGreet() {
        assertEquals("Hello, magma", App.greet());
    }
}
