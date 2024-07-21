package magma.app.compile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static magma.app.compile.Compiler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_LOWER_SYMBOL = "test";
    public static final String TEST_UPPER_SYMBOL = "Test";

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

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void interfaceName(String name) {
        assertCompile(renderInterface(name), renderTrait(Map.of(NAME, name)));
    }

    @Test
    void interfacePublic() {
        var map = Map.of(MODIFIERS, EXPORT_KEYWORD_WITH_SPACE, NAME, TEST_UPPER_SYMBOL);
        assertCompile(renderInterface(PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL), renderTrait(map));
    }

    @Test
    void rootMemberMultiple() {
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