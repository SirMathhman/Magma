package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_LOWER_SYMBOL = "test";
    public static final String TEST_UPPER_SYMBOL = "Test";

    private static void assertCompile(String input, String output) {
        try {
            assertEquals(output, new Compiler().compile(input));
        } catch (ParseException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void className(String name) {
        assertCompile(Compiler.renderClass("", name, ""), Compiler.renderFunction("", name, ""));
    }

    @Test
    void classPublic() {
        assertCompile(Compiler.renderClass(Compiler.PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""), Compiler.renderFunction(Compiler.EXPORT_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""));
    }

    @Test
    void classMemberInvalid() {
        assertThrows(ParseException.class, () -> {
            var rendered = Compiler.renderClass("", TEST_UPPER_SYMBOL, "test");
            new Compiler().compile(rendered);
        });
    }

    @Test
    void rootStatementMultiple() {
        var renderedPackage = Compiler.renderPackage(TEST_LOWER_SYMBOL);
        var renderedImport = Compiler.renderImport("", TEST_LOWER_SYMBOL, TEST_UPPER_SYMBOL);
        assertCompile(renderedPackage + renderedImport, renderedImport);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageName(String name) {
        assertCompile(Compiler.renderPackage(name), "");
    }

    @Test
    void importWhitespace() {
        var input = Compiler.renderImport(" ", TEST_LOWER_SYMBOL, TEST_UPPER_SYMBOL);
        var output = Compiler.renderImport("", TEST_LOWER_SYMBOL, TEST_UPPER_SYMBOL);
        assertCompile(input, output);
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void importChild(String name) {
        var input = Compiler.renderImport("", "parent", name);
        assertCompile(input, input);
    }

    @Test
    void importParent() {
        var input = Compiler.renderImport("", "foo", "Bar");
        assertCompile(input, input);
    }
}