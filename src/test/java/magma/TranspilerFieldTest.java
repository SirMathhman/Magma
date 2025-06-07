package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerFieldTest {
    @Test
    void transpilesFieldDeclarations() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    public int count;",
            "    private String name;",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    public count: number;",
            "    private name: string;",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void finalFieldsBecomeReadonly() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    public final int count;",
            "    private final String name;",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    public readonly count: number;",
            "    private readonly name: string;",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsFieldAssignments() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    private int count = 1;",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    private count: number;",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
