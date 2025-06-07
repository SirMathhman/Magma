package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TranspilerTest {

    @Test
    void removesPackageDeclaration() {
        String javaSrc = "package com.example;\n\npublic class Foo {}";
        String expected = "export default class Foo {}";
        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void transpilesClassDefinitionWithModifier() {
        String javaSrc = "public final class Bar {}";
        String expected = "export default class Bar {}";
        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

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
            "        // TODO",
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
            "        // TODO",
            "    }",
            "    fromWrapper(c: string): string {",
            "        // TODO",
            "    }",
            "    fromString(s: string): string {",
            "        // TODO",
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
            "        // TODO",
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
            "        // TODO",
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
            "        // TODO",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void transpilesFieldDeclarations() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    public int count;",
            "    private String name;",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    public count: number;",
            "    private name: string;",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesExtendsClause() {
        String javaSrc = String.join("\n",
            "public class Child extends Parent {",
            "}");

        String expected = String.join("\n",
            "export default class Child extends Parent {",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesImplementsClause() {
        String javaSrc = String.join("\n",
            "public class Service implements Runnable {",
            "}");

        String expected = String.join("\n",
            "export default class Service implements Runnable {",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void transpilesEnumDefinition() {
        String javaSrc = "public enum Color { RED, GREEN, BLUE }";
        String expected = "export enum Color { RED, GREEN, BLUE }";

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
