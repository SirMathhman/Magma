package magma;

import org.junit.jupiter.api.Test;

public class ArithmeticTest {

    @Test
    void literalAdd() {
        TestUtils.assertValid("1 + 2", "3");
    }

    @Test
    void varAddCompound() {
        TestUtils.assertValid("let mut x = 10; x += 5; x", "15");
    }

    @Test
    void cmpLessThan() {
        TestUtils.assertValid("1 < 2", "true");
        TestUtils.assertValid("3 < 2", "false");
    }

    @Test
    void addTypedLits() {
        TestUtils.assertValid("1U8 + 2U8", "3");
    }

    @Test
    void invalidAddTypes() {
        TestUtils.assertInvalid("true + 1");
    }
}
