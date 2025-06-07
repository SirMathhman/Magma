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
            "    bar(x: number): number {}",
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
            "    baz(): void {}",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
