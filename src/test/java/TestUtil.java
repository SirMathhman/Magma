import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Utility class providing helper methods for testing the Magma compiler.
 * Contains methods for testing both valid results and error cases.
 */
public class TestUtil {

    /**
     * Asserts that the given Magma code compiles to the expected C code.
     * This helper method is used for testing valid compilation scenarios.
     *
     * @param magmaCode The Magma source code to compile
     * @param expectedCCode The expected C code output
     * @param message The assertion message to display if the test fails
     */
    public static void assertCompiles(String magmaCode, String expectedCCode, String message) {
        // Compile the Magma code
        String actualCCode = Main.compile(magmaCode);
        
        // Assert that the actual C code matches the expected C code
        assertEquals(expectedCCode, actualCCode, message);
    }

    /**
     * Asserts that the given Magma code throws the expected exception when compiled.
     * This helper method is used for testing error handling scenarios.
     *
     * @param magmaCode The Magma source code that should cause an error
     * @param expectedExceptionClass The expected exception class
     * @param expectedErrorMessage A substring that should be contained in the exception message
     */
    public static void assertCompilationError(String magmaCode, Class<? extends Exception> expectedExceptionClass, String expectedErrorMessage) {
        // Assert that the expected exception is thrown
        Exception exception = assertThrows(expectedExceptionClass, () -> {
            Main.compile(magmaCode);
        });
        
        // Verify the exception message contains the expected error message
        String actualMessage = exception.getMessage();
        assert(actualMessage.contains(expectedErrorMessage));
    }
}