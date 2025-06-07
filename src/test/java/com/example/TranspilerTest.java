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
    void leavesValueAssignmentsAsTodo() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void set() {",
            "        x = 1;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    set(): void {",
            "        // TODO",
            "    }",
            "}");

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
    void finalFieldsBecomeReadonly() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    public final int count;",
            "    private final String name;",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    public readonly count: number;",
            "    private readonly name: string;",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsFieldAssignments() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    private int count = 1;",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    private count: number;",
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
    void transpilesInterfaceDefinition() {
        String javaSrc = "public interface Service {}";
        String expected = "export interface Service {}";

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

    @Test
    void convertsLambdasToArrowFunctions() {
        String javaSrc = "Runnable r = () -> {};";
        String expected = "Runnable r = () => {};";

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsAssignmentsInArrowFunctions() {
        String javaSrc = "Runnable r = () -> { int x = 0; x++; };";

        String expected = String.join("\n",
            "Runnable r = () => {",
            "    let x: number = /* TODO */;",
            "    // TODO",
            "};");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsOneTodoPerStatement() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    int multi() {",
            "        int y = 0;",
            "        y++;",
            "        return y;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    multi(): number {",
            "        let y: number = /* TODO */;",
            "        // TODO",
            "        return /* TODO */;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsIfStatements() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void check(int x) {",
            "        if (x > 0) {",
            "            System.out.println(x);",
            "        }",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    check(x: number): void {",
            "        if (/* TODO */) {",
            "            // TODO",
            "        }",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsWhileStatements() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void loop() {",
            "        while (true) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    loop(): void {",
            "        while (/* TODO */) {",
            "            // TODO",
            "        }",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
