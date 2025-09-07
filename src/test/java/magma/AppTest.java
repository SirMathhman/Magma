package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    @Test
    public void greetShouldReturnHelloName() {
        assertEquals("Hello, Alice!", App.greet("Alice"));
    }
}
