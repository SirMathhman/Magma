package magma.app.compile;

import magma.api.Result;
import magma.api.Results;
import magma.app.ApplicationException;
import magma.app.compile.lang.CommonLang;
import magma.app.compile.lang.JavaLang;
import magma.app.compile.lang.MagmaLang;
import magma.app.compile.rule.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.app.compile.lang.JavaLang.METHOD;
import static magma.app.compile.lang.MagmaLang.FUNCTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_LOWER_SYMBOL = "test";
    public static final String TEST_UPPER_SYMBOL = "Test";

    private static void assertCompile(String input, String output) {
        try {
            Result<String, ApplicationException> stringCompileExceptionResult = Compiler.compile(input);
            assertEquals(output, Results.unwrap(stringCompileExceptionResult));
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    static String renderPackage(String name) {
        return JavaLang.PACKAGE_KEYWORD_WITH_SPACE + name + Splitter.STATEMENT_END;
    }

    static String renderImport(String whitespace, String parent, String child) {
        return whitespace + CommonLang.IMPORT_KEYWORD_WITH_SPACE + parent + "." + child + Splitter.STATEMENT_END;
    }

    static String renderMethod(String name) {
        return JavaLang.VOID_KEYWORD_WITH_SPACE + name + JavaLang.METHOD_SUFFIX;
    }

    static String renderJavaClass(String modifiers, String name, String content) {
        return modifiers + CommonLang.CLASS_KEYWORD_WITH_SPACE + name + " " + Splitter.BLOCK_START + content + Splitter.BLOCK_END;
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void methodName(String name) throws ApplicationException {
        var input = renderJavaClass("", TEST_UPPER_SYMBOL, renderMethod(name));

        var child = new Node()
                .retype(METHOD)
                .withString(CommonLang.NAME, name);

        var node = new Node()
                .retype(FUNCTION)
                .withString(CommonLang.MODIFIERS, CommonLang.CLASS_KEYWORD_WITH_SPACE)
                .withString(CommonLang.NAME, TEST_UPPER_SYMBOL)
                .withNode(CommonLang.CONTENT, child);

        Rule statement = MagmaLang.createStatementRule();
        Rule rule = MagmaLang.createFunctionRule(statement);
        var generated = rule.generate(node);
        var output = Results.unwrap(generated.result().mapErr(err -> Compiler.wrapErr(generated)));
        assertCompile(input, output);
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void className(String name) throws ApplicationException {
        var node = new Node()
                .retype(FUNCTION)
                .withString(CommonLang.MODIFIERS, CommonLang.CLASS_KEYWORD_WITH_SPACE)
                .withString(CommonLang.NAME, name);

        var input = renderJavaClass("", name, "");
        Rule statement = MagmaLang.createStatementRule();
        Rule rule = MagmaLang.createFunctionRule(statement);
        var generated = rule.generate(node);
        var output = Results.unwrap(generated.result().mapErr(err -> Compiler.wrapErr(generated)));
        assertCompile(input, output);
    }

    @Test
    void classPublic() throws ApplicationException {
        var node = new Node()
                .retype(FUNCTION)
                .withString(CommonLang.MODIFIERS, MagmaLang.EXPORT_KEYWORD_WITH_SPACE + CommonLang.CLASS_KEYWORD_WITH_SPACE)
                .withString(CommonLang.NAME, TEST_UPPER_SYMBOL)
                .withString(CommonLang.CONTENT, "");

        Rule statement = MagmaLang.createStatementRule();
        Rule rule = MagmaLang.createFunctionRule(statement);
        var generate = rule.generate(node);
        assertCompile(renderJavaClass(JavaLang.PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""), Results.unwrap(generate.result().mapErr(err -> Compiler.wrapErr(generate))));
    }

    @Test
    void classMemberInvalid() {
        assertThrows(ApplicationException.class, () -> {
            var rendered = renderJavaClass("", TEST_UPPER_SYMBOL, "test");
            var stringCompileExceptionResult = Compiler.compile(rendered);
            Results.unwrap(stringCompileExceptionResult);
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