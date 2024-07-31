package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void test(String name) {
        assertCompile(Compiler.renderPackage(name), "");
    }

    private static void assertCompile(String input, String output) {
        try {
            assertEquals(output, new Compiler().compile(input));
        } catch (ParseException e) {
            fail(e);
        }
    }
}