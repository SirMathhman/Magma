package magma.app.compile.lang.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OperatorOpeningParenthesesLocatorTest {
    @Test
    void test() {
        var value = new OperatorOpeningParenthesesLocator()
                .locate("foo(\"\",((\"\\\"\",((\"\"), \"\\\"\"))))")
                .findFirst()
                .orElseThrow();

        assertEquals(3, value);
    }
}