package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerClassTest {
    @Test
    void removesPackageDeclaration() {
        var javaSrc = "package com.example;\n\npublic class Foo {}";
        var expected = "export default class Foo {}";
        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void transpilesClassDefinitionWithModifier() {
        var javaSrc = "public final class Bar {}";
        var expected = "export default class Bar {}";
        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesExtendsClause() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Child extends Parent {",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Child extends Parent {",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesImplementsClause() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Service implements Runnable {",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Service implements Runnable {",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void transpilesInterfaceDefinition() {
        var javaSrc = "public interface Service {}";
        var expected = "export interface Service {}";

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void transpilesEnumDefinition() {
        var javaSrc = "public enum Color { RED, GREEN, BLUE }";
        var expected = "export enum Color { RED, GREEN, BLUE }";

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
