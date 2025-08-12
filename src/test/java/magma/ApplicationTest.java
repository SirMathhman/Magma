package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {
    @Test
    void alwaysThrows_shouldThrowException() {
        Application app = new Application();
        assertThrows(ApplicationException.class, () -> {
            app.alwaysThrows();
        });
    }
}
