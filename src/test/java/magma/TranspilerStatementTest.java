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
                "        doThing(/* TODO */, new Some<>(1));",
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
                "        let x: number = doThing(/* TODO */, new Some<>(1));",
                "    }",
                "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsConstructorCalls() {
        String javaSrc = String.join("\n",
                "public class Foo {",
                "    void build() {",
                "        new Bar(1, 2);",
                "    }",
                "}");

        String expected = String.join("\n",
                "export default class Foo {",
                "    build(): void {",
                "        new Bar(1, 2);",
                "    }",
                "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsConstructorCallsInLetStatements() {
        String javaSrc = String.join("\n",
                "public class Foo {",
                "    void make() {",
                "        Bar b = new Bar(1);",
                "    }",
                "}");

        String expected = String.join("\n",
                "export default class Foo {",
                "    make(): void {",
                "        let b: any = new Bar(1);",
                "    }",
                "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesCallsOnNewInstances() {
        String javaSrc = String.join("\n",
                "public class Foo {",
                "    void run() {",
                "        new Main().run();",
                "    }",
                "}");

        String expected = String.join("\n",
                "export default class Foo {",
                "    run(): void {",
                "        new Main().run();",
                "    }",
                "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesCallsOnNewInstancesInLetStatements() {
        String javaSrc = String.join("\n",
                "public class Foo {",
                "    void run() {",
                "        Option<String> error = new Main().run();",
                "    }",
                "}");

        String expected = String.join("\n",
                "export default class Foo {",
                "    run(): void {",
                "        let error: Option<string> = new Main().run();",
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
            "    let x: number = 0;",
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
            "        let y: number = 0;",
            "        // TODO",
            "        return y;",
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

    @Test
    void keepsNumericValues() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void show() {",
            "        int num = 7;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    show(): void {",
            "        let num: number = 7;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesMemberAccessInAssignments() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void run(Parent p) {",
            "        int x = p.count;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    run(p: any): void {",
            "        let x: number = p.count;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesMemberAccessInReturns() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    int get(Parent p) {",
            "        return p.count;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    get(p: any): number {",
            "        return p.count;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesNestedValuesRecursively() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void run() {",
            "        int x = doThing(a, new Some<>(make(1, 2)));",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    run(): void {",
            "        let x: number = doThing(/* TODO */, new Some<>(make(1, 2)));",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesMemberAccessAfterInvokable() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void run() {",
            "        int x = doStuff().myField;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    run(): void {",
            "        let x: number = doStuff().myField;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void keepsIdentifierValues() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void copy(int src) {",
            "        int x = src;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    copy(src: number): void {",
            "        let x: number = src;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesDeepChainedAccess() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void run() {",
            "        int x = first.second().third.fourth;",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    run(): void {",
            "        let x: number = first.second().third.fourth;",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesInvokableInIfCondition() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void check() {",
            "        if (isValid(run())) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    check(): void {",
            "        if (isValid(run())) {",
            "            // TODO",
            "        }",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesMemberAccessInWhileCondition() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void loop(Iter it) {",
            "        while (it.hasNext()) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    loop(it: any): void {",
            "        while (it.hasNext()) {",
            "            // TODO",
            "        }",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesNotOperatorInIfCondition() {
        String javaSrc = String.join("\n",
            "public class Foo {",
            "    void check() {",
            "        if (!isValid(1)) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        String expected = String.join("\n",
            "export default class Foo {",
            "    check(): void {",
            "        if (!/* TODO */(1)) {",
            "            // TODO",
            "        }",
            "    }",
            "}");

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
