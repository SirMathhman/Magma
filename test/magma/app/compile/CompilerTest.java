package magma.app.compile;

import magma.app.compile.rule.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.Optional;

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
    void interfaceName(String name) throws CompileException {
        var node = new Node().with(NAME, name);

        assertCompile(INTERFACE_RULE.generate(node.retype(INTERFACE)).$(),
                STRUCT_RULE.generate(node.retype(STRUCT)).$());
    }

    @Test
    void interfacePublic() throws CompileException {
        var node = new Node()
                .retype(INTERFACE)
                .with(MODIFIERS, PUBLIC_KEYWORD_WITH_SPACE)
                .with(NAME, TEST_UPPER_SYMBOL);
        var interfaceString = INTERFACE_RULE.generate(node).$();

        var structNode = new Node()
                .retype(STRUCT)
                .with(MODIFIERS, EXPORT_KEYWORD_WITH_SPACE)
                .with(NAME, TEST_UPPER_SYMBOL);
        var structString = STRUCT_RULE.generate(structNode).$();

        assertCompile(interfaceString, structString);
    }

    @Test
    void rootMemberMultiple() throws CompileException {
        var rule = createImportRule();
        var node = new Node().retype(IMPORT).with(SEGMENTS, TEST_LOWER_SYMBOL);
        var importString = rule.generate(node).$();
        assertCompile(renderPackageStatement(TEST_LOWER_SYMBOL) + importString, importString);
    }

    @Test
    void importStripLeading() throws CompileException {
        var rule = createImportRule();
        var withoutLeading = new Node().retype(IMPORT).with(SEGMENTS, TEST_LOWER_SYMBOL);
        var withLeading = withoutLeading.with(LEADING, " ");

        var input = rule.generate(withLeading).$();
        var output = rule.generate(withoutLeading).$();
        assertCompile(input, output);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void importName(String name) throws CompileException {
        var rule = createImportRule();
        var node = new Node()
                .retype(IMPORT)
                .with(SEGMENTS, name);

        var input = rule.generate(node).$();
        assertCompile(input, input);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageName(String name) {
        assertCompile(renderPackageStatement(name), "");
    }
}