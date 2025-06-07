package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerFieldTest {
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
}
