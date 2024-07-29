package magma.app.compile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.app.compile.Compiler.IMPORT_KEYWORD_WITH_SPACE;
import static magma.app.compile.Compiler.NAME;
import static magma.app.compile.Compiler.PACKAGE_KEYWORD_WITH_SPACE;
import static magma.app.compile.Splitter.STATEMENT_END;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    public static final String TEST_LOWER_SYMBOL = "test";
    public static final String TEST_UPPER_SYMBOL = "Test";

    private static void assertCompile(String input, String expected) {
        try {
            var actual = new Compiler().compile(input);
            assertEquals(expected, actual);
        } catch (CompileException e) {
            fail(e);
        }
    }

    private static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    private static String renderImport(String whitespace, String name) {
        return whitespace + IMPORT_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    private static String renderInterface(String modifiers, String name, String members) {
        return modifiers + Compiler.INTERFACE_KEYWORD_WITH_SPACE + name + " " + Splitter.BLOCK_START + members + Splitter.BLOCK_END;
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void interfaceMember(String name) {
        var input = renderInterface("", TEST_UPPER_SYMBOL, Compiler.renderMethod(name));
        var members0 = new SuffixRule(new StringRule(Compiler.NAME), Compiler.DEFINITION_SUFFIX)
                .generate(new Node().withString(NAME, name))
                .orElseThrow();

        Node node = new Node()
                .withString(Compiler.MODIFIERS, "")
                .withString(Compiler.NAME, TEST_UPPER_SYMBOL)
                .withString(Compiler.MEMBERS, members0);
        var output = Compiler.createTraitRule()
                .generate(node)
                .orElseThrow();

        assertCompile(input, output);
    }

    @Test
    void interfacePublic() {
        Node node = new Node()
                .withString(Compiler.MODIFIERS, Compiler.EXPORT_KEYWORD_WITH_SPACE)
                .withString(Compiler.NAME, TEST_UPPER_SYMBOL);

        assertCompile(renderInterface(Compiler.PUBLIC_KEYWORD_WITH_SPACE, TEST_UPPER_SYMBOL, ""), Compiler.createTraitRule()
                .generate(node)
                .orElseThrow());
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void interfaceName(String name) {
        Node node = new Node()
                .withString(Compiler.MODIFIERS, "")
                .withString(Compiler.NAME, name);

        assertCompile(renderInterface("", name, ""), Compiler.createTraitRule()
                .generate(node)
                .orElseThrow());
    }

    @Test
    void rootMemberMultiple() {
        var renderedImport = renderImport("", "foo");
        assertCompile(renderPackage(TEST_LOWER_SYMBOL) + renderedImport, renderedImport);
    }

    @Test
    void importPadLeft() {
        assertCompile(renderImport(" ", TEST_LOWER_SYMBOL), renderImport("", TEST_LOWER_SYMBOL));
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