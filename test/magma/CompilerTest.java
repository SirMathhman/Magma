package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.Compiler.STATEMENT_END;
import static magma.Compiler.renderImport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {

    public static final String TEST_LOWER_SYMBOL = "test";

    private static void assertCompile(String input, String output) {
        try {
            var value = Compiler.compile(input);
            assertEquals(output, value);
        } catch (CompileException e) {
            fail(e);
        }
    }

    private static String renderPackageStatement(String name) {
        return Compiler.PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    @Test
    void multiple() {
        assertCompile(renderPackageStatement(TEST_LOWER_SYMBOL) + renderImport(TEST_LOWER_SYMBOL), renderImport(TEST_LOWER_SYMBOL));
    }

    @Test
    void importStripLeading() {
        assertCompile(Compiler.renderImport(" ", TEST_LOWER_SYMBOL), Compiler.renderImport(TEST_LOWER_SYMBOL));
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void importName(String name) {
        var input = Compiler.renderImport(name);
        assertCompile(input, input);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageName(String name) {
        assertCompile(renderPackageStatement(name), "");
    }
}