package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerStatementTest {

    @Test
    void stubsInvokables() {
        String javaSrc = String.join("\n",
                "public class Foo {",
                "    void run() {",
                "        doThing(a, new Some<>(1));",
                "    }",
                "}");

        String expected = String.join("\n",
                "export default class Foo {",
                "    run(): void {",
                "        /* TODO */(/* TODO */, /* TODO */);",
                "    }",
                "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsInvokablesInLetStatements() {
        String javaSrc = String.join("\n",
                "public class Foo {",
                "    void run() {",
                "        int x = doThing(a, new Some<>(1));",
                "    }",
                "}");

        String expected = String.join("\n",
                "export default class Foo {",
                "    run(): void {",
                "        let x: number = /* TODO */(/* TODO */, /* TODO */);",
                "    }",
                "}");

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

    @Test
    void keepsStringValues() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void show() {",
            "        String msg = \"hi\";",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    show(): void {",
            "        let msg: string = \"hi\";",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
