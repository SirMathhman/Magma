package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.Compiler.FUNCTION;
import static magma.Compiler.METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_LOWER_SYMBOL = "test";
    public static final String TEST_UPPER_SYMBOL = "Test";

    private static void assertCompile(String input, String output) {
        try {
            assertEquals(output, new Compiler().compile(input));
        } catch (CompileException e) {
            fail(e);
        }
    }

    static String renderPackage(String name) {
        return Compiler.PACKAGE_KEYWORD_WITH_SPACE + name + Splitter.STATEMENT_END;
    }

    static String renderImport(String whitespace, String parent, String child) {
        return whitespace + Compiler.IMPORT_KEYWORD_WITH_SPACE + parent + "." + child + Splitter.STATEMENT_END;
    }

    static String renderMethod(String name) {
        return Compiler.VOID_KEYWORD_WITH_SPACE + name + Compiler.METHOD_SUFFIX;
    }

    static String renderJavaClass(String modifiers, String name, String content) {
        return modifiers + Compiler.CLASS_KEYWORD_WITH_SPACE + name + " " + Splitter.BLOCK_START + content + Splitter.BLOCK_END;
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void methodName(String name) throws GenerateException {
        var input = renderJavaClass("", TEST_UPPER_SYMBOL, renderMethod(name));

        var child = new Node()
                .retype(METHOD)
                .withString(Compiler.NAME, name);

        var node = new Node()
                .retype(FUNCTION)
                .withString(Compiler.MODIFIERS, Compiler.CLASS_KEYWORD_WITH_SPACE)
                .withString(Compiler.NAME, TEST_UPPER_SYMBOL)
                .withNode(Compiler.CONTENT, child);

        Rule statement = Compiler.createStatementRule();
        var output = Compiler.createFunctionRule(statement).generate(node).$();
        assertCompile(input, output);
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void className(String name) throws GenerateException {
        var node = new Node()
                .retype(FUNCTION)
                .withString(Compiler.MODIFIERS, Compiler.CLASS_KEYWORD_WITH_SPACE)
                .withString(Compiler.NAME, name);

        var input = renderJavaClass("", name, "");
        Rule statement = Compiler.createStatementRule();
        var output = Compiler.createFunctionRule(statement).generate(node).$();

        assertCompile(input, output);
    }

    @Test
    void classPublic() throws GenerateException {
        var node = new Node()
                .retype(FUNCTION)
                .withString(Compiler.MODIFIERS, Compiler.EXPORT_KEYWORD_WITH_SPACE + Compiler.CLASS_KEYWORD_WITH_SPACE)
                .withString(Compiler.NAME, TEST_UPPER_SYMBOL)
                .withString(Compiler.CONTENT, "");

        Rule statement = Compiler.createStatementRule();
        assertCompile(renderJavaClass(Compiler.PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""), Compiler.createFunctionRule(statement).generate(node).$());
    }

    @Test
    void classMemberInvalid() {
        assertThrows(ParseException.class, () -> {
            var rendered = renderJavaClass("", TEST_UPPER_SYMBOL, "test");
            new Compiler().compile(rendered);
        });
    }

    @Test
    void rootStatementMultiple() {
        var renderedPackage = renderPackage(TEST_LOWER_SYMBOL);
        var renderedImport = renderImport("", TEST_LOWER_SYMBOL, TEST_UPPER_SYMBOL);
        assertCompile(renderedPackage + renderedImport, renderedImport);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageName(String name) {
        assertCompile(renderPackage(name), "");
    }

    @Test
    void importWhitespace() {
        var input = renderImport(" ", TEST_LOWER_SYMBOL, TEST_UPPER_SYMBOL);
        var output = renderImport("", TEST_LOWER_SYMBOL, TEST_UPPER_SYMBOL);
        assertCompile(input, output);
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void importChild(String name) {
        var input = renderImport("", "parent", name);
        assertCompile(input, input);
    }

    @Test
    void importParent() {
        var input = renderImport("", "foo", "Bar");
        assertCompile(input, input);
    }
}