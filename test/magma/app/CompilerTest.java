package magma.app;

import magma.compile.CompileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.app.Compiler.IMPORT_KEYWORD_WITH_SPACE;
import static magma.app.Compiler.PACKAGE_KEYWORD_WITH_SPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_LOWER_BOUND = "test";

    private static void assertCompile(String input, String expected) {
        try {
            var actual = new Compiler().compile(input);
            assertEquals(expected, actual);
        } catch (CompileException e) {
            fail(e);
        }
    }

    private static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + Compiler.STATEMENT_END;
    }

    private static String renderImport(String whitespace, String name) {
        return whitespace + IMPORT_KEYWORD_WITH_SPACE + name + Compiler.STATEMENT_END;
    }

    @Test
    void rootMemberMultiple() {
        var renderedImport = renderImport("", "foo");
        assertCompile(renderPackage(TEST_LOWER_BOUND) + renderedImport, renderedImport);
    }

    @Test
    void importPadLeft() {
        assertCompile(renderImport(" ", TEST_LOWER_BOUND), renderImport("", TEST_LOWER_BOUND));
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void importStatement(String name) {
        var statement = renderImport("", name);
        assertCompile(statement, statement);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageStatement(String name) {
        assertCompile(renderPackage(name), "");
    }
}