package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

class BasicCompilerTest {
    @Test
    void valid() {
        assertValid("", "");
    }

    @Test
    void invalid() {
        assertInvalid("?");
    }
}