package magma.app.compile;

import magma.app.compile.rule.Node;
import magma.app.compile.rule.Rule;
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
        assertCompile(INTERFACE_RULE.generate(new Node(Map.of(NAME, name))).orElseThrow(), STRUCT_RULE.generate(new Node(Map.of(NAME, name))).orElseThrow());
    }

    @Test
    void interfacePublic() {
        var map = Map.of(MODIFIERS, EXPORT_KEYWORD_WITH_SPACE, NAME, TEST_UPPER_SYMBOL);
        assertCompile(INTERFACE_RULE.generate(new Node(Map.of(MODIFIERS, PUBLIC_KEYWORD_WITH_SPACE, NAME, TEST_UPPER_SYMBOL))).orElseThrow(), STRUCT_RULE.generate(new Node(map)).orElseThrow());
    }

    @Test
    void rootMemberMultiple() {
        Rule rule = createImportRule();
        Rule rule1 = createImportRule();
        assertCompile(renderPackageStatement(TEST_LOWER_SYMBOL) + rule1.generate(new Node(Map.of(SEGMENTS, TEST_LOWER_SYMBOL)))
                .orElseThrow(), rule.generate(new Node(Map.of(SEGMENTS, TEST_LOWER_SYMBOL)))
                .orElseThrow());
    }

    @Test
    void importStripLeading() {
        Rule rule = createImportRule();
        Rule rule1 = createImportRule();
        assertCompile(rule1.generate(new Node(Map.of(LEADING, " ", SEGMENTS, TEST_LOWER_SYMBOL)))
                .orElseThrow(), rule.generate(new Node(Map.of(SEGMENTS, TEST_LOWER_SYMBOL)))
                .orElseThrow());
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void importName(String name) {
        Rule rule = createImportRule();
        var input = rule.generate(new Node(Map.of(SEGMENTS, name)))
                .orElseThrow();
        assertCompile(input, input);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageName(String name) {
        assertCompile(renderPackageStatement(name), "");
    }
}