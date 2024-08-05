package magma.app.compile.lang.magma;

import magma.api.UnsafeException;
import magma.app.compile.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagmaDefinitionTest {
    @Test
    void name() {
        assertGenerate("test", createNode()
                .withString("name", "test"));
    }

    private static Node createNode() {
        return new Node(MagmaDefinition.DEFINITION_TYPE);
    }

    @Test
    void empty() {
        assertGenerate("()", createNode());
    }

    private static void assertGenerate(String output, Node input) {
        try {
            var actual = MagmaDefinition.createRule()
                    .generate(input)
                    .result()
                    .$();
            assertEquals(output, actual);
        } catch (UnsafeException e) {
            fail(e);
        }
    }
}