package magma.app.compile;

import magma.api.result.Results;
import magma.app.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.app.compile.Compiler.EXPORT_KEYWORD_WITH_SPACE;
import static magma.app.compile.Compiler.PUBLIC_KEYWORD_WITH_SPACE;
import static magma.app.compile.Compiler.compile;
import static magma.app.compile.Compiler.renderClass;
import static magma.app.compile.Compiler.renderFunction;
import static magma.app.compile.Compiler.renderImport;
import static magma.app.compile.Compiler.renderPackage;
import static magma.app.compile.Compiler.renderRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_UPPER_SYMBOL = "Test";

    private static void assertCompile(String input, String expected) {
        try {
            var actual = Results.unwrap(compile(input));
            assertEquals(expected, actual);
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void recordName(String name) {
        assertCompile(renderRecord("", name), renderFunction("", name, ""));
    }

    @Test
    void recordPublic() {
        assertCompile(renderRecord(PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL), renderFunction(EXPORT_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void className(String name) {
        assertCompile(renderClass("", name), renderFunction("", name, ""));
    }

    @Test
    void classPublic() {
        assertCompile(
                renderClass(PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL),
                renderFunction(EXPORT_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""));
    }

    @Test
    void rootMemberStrip() {
        var input = renderImport("\n", "second", "");
        var output = renderImport("", "second", "");
        assertCompile(input, output);
    }

    @Test
    void rootMemberMultiple() {
        var first = renderPackage("");
        var second = renderImport("", "second", "Sibling");
        assertCompile(first + second, second);
    }
}
