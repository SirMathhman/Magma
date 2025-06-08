package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerMethodTest {
    @Test
    void stubsMethodBodiesPreservingNames() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    int bar(long x) {",
            "        return 42;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    bar(x: number): number {",
            "        return 42;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsVoidReturnTypes() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void baz() {",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    baz(): void {",
            "        // TODO",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsCharCharacterAndStringToString() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    char fromChar(char c) {",
            "        return c;",
            "    }",
            "    char fromWrapper(Character c) {",
            "        return c;",
            "    }",
            "    String fromString(String s) {",
            "        return s;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    fromChar(c: string): string {",
            "        return c;",
            "    }",
            "    fromWrapper(c: string): string {",
            "        return c;",
            "    }",
            "    fromString(s: string): string {",
            "        return s;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsArrayTypes() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    int[] bar(String[] words) {",
            "        return null;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    bar(words: string[]): number[] {",
            "        return /* TODO */;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsBooleanTypes() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    Boolean flag(Boolean a, boolean b) {",
            "        return a;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    flag(a: boolean, b: boolean): boolean {",
            "        return a;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsBoxedNumberTypes() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    Double sum(Integer a, Long b, Float c, Short d) {",
            "        return 1.0;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    sum(a: number, b: number, c: number, d: number): number {",
            "        return 1.0;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsGenericTypes() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    List<String> names(List<String> in) {",
            "        return in;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    names(in: List<string>): List<string> {",
            "        return in;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsFunctionInterfaceTypes() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    java.util.function.Function<Integer,String> map(java.util.function.Function<Integer,String> fn) {",
            "        return fn;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    map(fn: (arg0: number) => string): (arg0: number) => string {",
            "        return fn;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
