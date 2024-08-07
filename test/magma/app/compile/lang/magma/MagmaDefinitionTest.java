package magma.app.compile.lang.magma;

import magma.app.compile.Node;
import org.junit.jupiter.api.Test;

import java.util.List;

import static magma.app.compile.lang.common.Modifiers.MODIFIERS;
import static magma.app.compile.lang.magma.MagmaDefinition.NAME;
import static magma.app.compile.lang.magma.MagmaDefinition.TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MagmaDefinitionTest {
    private static Node createNode() {
        return new Node(MagmaDefinition.DEFINITION);
    }

    private static void assertGenerate(String output, Node input) {
        MagmaDefinition.createRule()
                .generate(input)
                .result()
                .match(actual -> {
                    assertEquals(output, actual);
                    return null;
                }, err -> fail(err.toString()));
    }

    private static Node createSymbolRuleType(String value) {
        return new Node("symbol").withString("value", value);
    }

    @Test
    void all() {
        assertGenerate("public caller(value : I32) : I64", createNode()
                .withString(NAME, "caller")
                .withStringList(MODIFIERS, List.of("public"))
                .withNode(TYPE, createSymbolRuleType("I64"))
                .withNodeList(MagmaDefinition.PARAMS, List.of(createNode()
                        .withString(NAME, "value")
                        .withNode(TYPE, createSymbolRuleType("I32"))
                )));
    }

    @Test
    void parameters() {
        assertGenerate("caller(value : I32)", createNode()
                .withString(NAME, "caller")
                .withNodeList(MagmaDefinition.PARAMS, List.of(createNode()
                        .withString(NAME, "value")
                        .withNode(TYPE, createSymbolRuleType("I32"))
                )));
    }

    @Test
    void modifiers() {
        assertGenerate("let temp", createNode()
                .withString(NAME, "temp")
                .withStringList(MODIFIERS, List.of("let")));
    }

    @Test
    void type() {
        assertGenerate("value : I32", createNode()
                .withString(NAME, "value")
                .withNode(MagmaDefinition.TYPE, createSymbolRuleType("I32")));
    }

    @Test
    void name() {
        assertGenerate("test", createNode()
                .withString(NAME, "test"));
    }

    @Test
    void empty() {
        assertGenerate("()", createNode());
    }
}