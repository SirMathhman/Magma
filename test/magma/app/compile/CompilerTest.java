package magma.app.compile;

import magma.api.result.Results;
import magma.app.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_UPPER_SYMBOL = "Test";

    private static void assertCompile(String input, String expected) {
        try {
            var actual = Results.unwrap(Compiler.compile(input));
            assertEquals(expected, actual);
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void className(String name) {
        assertCompile(Compiler.renderClass("", name), Compiler.renderFunction("", name, ""));
    }

    @Test
    void classPublic() {
        assertCompile(
                Compiler.renderClass(Compiler.PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL),
                Compiler.renderFunction(Compiler.EXPORT_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""));
    }

    @Test
    void rootMemberStrip() {
        var input = Compiler.renderImport("\n", "second", "");
        var output = Compiler.renderImport("", "second", "");
        assertCompile(input, output);
    }

    @Test
    void rootMemberMultiple() {
        var first = Compiler.renderPackage("");
        var second = Compiler.renderImport("", "second", "Sibling");
        assertCompile(first + second, second);
    }
}
