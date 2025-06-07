package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TranspilerClassTest {
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
}
