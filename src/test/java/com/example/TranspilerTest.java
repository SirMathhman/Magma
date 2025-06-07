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
}
