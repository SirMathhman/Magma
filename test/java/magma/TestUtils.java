package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Utility class for Magma test methods.
 */
public class TestUtils {
    /**
     * Asserts that running the given Magma code produces the expected output.
     *
     * @param input The Magma code to run
     * @param output The expected output
     */
    public static void assertRun(String input, String output) {
        assertEquals(output, Main.run(input));
    }
}