package magma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class MainTest {
    private static void assertDivideStatements(String input, List<String> output) {
        List<String> unwrapped = Lists.unwrap(Main.divide(input, new Main.DecoratedDivider(Main::divideStatementChar)));
        Assertions.assertIterableEquals(output, unwrapped);
    }

    @Test
    void divideSingleQuotes() {
        assertDivideStatements("';'", Collections.singletonList("';'"));
    }

    @Test
    void divideSimpleStatement() {
        assertDivideStatements("test", Collections.singletonList("test"));
    }
}
