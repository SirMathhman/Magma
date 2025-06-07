package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerMethodTest {
    @Test
    void stubsMethodBodiesPreservingNames() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    int bar(long x) {",
            "        return 42;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    bar(x: number): number {",
            "        return /* TODO */;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsVoidReturnTypes() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void baz() {",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    baz(): void {",
            "        // TODO",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsCharCharacterAndStringToString() {
        String javaSrc = String.join("\n",
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

        String expected = String.join("\n",
            "export default class Foo {",
            "    fromChar(c: string): string {",
            "        return /* TODO */;",
            "    }",
            "    fromWrapper(c: string): string {",
            "        return /* TODO */;",
            "    }",
            "    fromString(s: string): string {",
            "        return /* TODO */;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsArrayTypes() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    int[] bar(String[] words) {",
            "        return null;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    bar(words: string[]): number[] {",
            "        return /* TODO */;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsBooleanTypes() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    Boolean flag(Boolean a, boolean b) {",
            "        return a;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    flag(a: boolean, b: boolean): boolean {",
            "        return /* TODO */;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void mapsGenericTypes() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    List<String> names(List<String> in) {",
            "        return in;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    names(in: List<string>): List<string> {",
            "        return /* TODO */;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
