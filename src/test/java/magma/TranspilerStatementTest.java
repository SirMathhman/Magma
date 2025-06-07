package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerStatementTest {

    @Test
    void stubsInvokables() {
        var javaSrc = String.join(System.lineSeparator(),
                "public class Foo {",
                "    void run() {",
                "        doThing(a, new Some<>(1));",
                "    }",
                "}");

        var expected = String.join(System.lineSeparator(),
                "export default class Foo {",
                "    run(): void {",
                "        doThing(a, new Some<>(1));",
                "    }",
                "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsInvokablesInLetStatements() {
        var javaSrc = String.join(System.lineSeparator(),
                "public class Foo {",
                "    void run() {",
                "        int x = doThing(a, new Some<>(1));",
                "    }",
                "}");

        var expected = String.join(System.lineSeparator(),
                "export default class Foo {",
                "    run(): void {",
                "        let x: number = doThing(a, new Some<>(1));",
                "    }",
                "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsConstructorCalls() {
        var javaSrc = String.join(System.lineSeparator(),
                "public class Foo {",
                "    void build() {",
                "        new Bar(1, 2);",
                "    }",
                "}");

        var expected = String.join(System.lineSeparator(),
                "export default class Foo {",
                "    build(): void {",
                "        new Bar(1, 2);",
                "    }",
                "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsConstructorCallsInLetStatements() {
        var javaSrc = String.join(System.lineSeparator(),
                "public class Foo {",
                "    void make() {",
                "        Bar b = new Bar(1);",
                "    }",
                "}");

        var expected = String.join(System.lineSeparator(),
                "export default class Foo {",
                "    make(): void {",
                "        let b: Bar = new Bar(1);",
                "    }",
                "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesCallsOnNewInstances() {
        var javaSrc = String.join(System.lineSeparator(),
                "public class Foo {",
                "    void run() {",
                "        new Main().run();",
                "    }",
                "}");

        var expected = String.join(System.lineSeparator(),
                "export default class Foo {",
                "    run(): void {",
                "        new Main().run();",
                "    }",
                "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesCallsOnNewInstancesInLetStatements() {
        var javaSrc = String.join(System.lineSeparator(),
                "public class Foo {",
                "    void run() {",
                "        Option<String> error = new Main().run();",
                "    }",
                "}");

        var expected = String.join(System.lineSeparator(),
                "export default class Foo {",
                "    run(): void {",
                "        let error: Option<string> = new Main().run();",
                "    }",
                "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void leavesValueAssignmentsAsTodo() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void set() {",
            "        x = 1;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    set(): void {",
            "        // TODO",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void convertsLambdasToArrowFunctions() {
        var javaSrc = "Runnable r = () -> {};";
        var expected = "Runnable r = () => {};";

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsAssignmentsInArrowFunctions() {
        var javaSrc = "Runnable r = () -> { int x = 0; x++; };";

        var expected = String.join(System.lineSeparator(),
            "Runnable r = () => {",
            "    let x: number = 0;",
            "    // TODO",
            "};");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsOneTodoPerStatement() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    int multi() {",
            "        int y = 0;",
            "        y++;",
            "        return y;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    multi(): number {",
            "        let y: number = 0;",
            "        // TODO",
            "        return y;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsIfStatements() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void check(int x) {",
            "        if (x > 0) {",
            "            System.out.println(x);",
            "        }",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    check(x: number): void {",
            "        if (/* TODO */) {",
            "            System.out.println(x);",
            "        }",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void stubsWhileStatements() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void loop() {",
            "        while (true) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    loop(): void {",
            "        while (/* TODO */) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }


    @Test
    void keepsStringValues() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void show() {",
            "        String msg = \"hi\";",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    show(): void {",
            "        let msg: string = \"hi\";",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void keepsNumericValues() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void show() {",
            "        int num = 7;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    show(): void {",
            "        let num: number = 7;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesMemberAccessInAssignments() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void run(Parent p) {",
            "        int x = p.count;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    run(p: Parent): void {",
            "        let x: number = p.count;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesMemberAccessInReturns() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    int get(Parent p) {",
            "        return p.count;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    get(p: Parent): number {",
            "        return p.count;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesNestedValuesRecursively() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void run() {",
            "        int x = doThing(a, new Some<>(make(1, 2)));",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    run(): void {",
            "        let x: number = doThing(a, new Some<>(make(1, 2)));",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void preservesMemberAccessAfterInvokable() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void run() {",
            "        int x = doStuff().myField;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    run(): void {",
            "        let x: number = doStuff().myField;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void keepsIdentifierValues() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void copy(int src) {",
            "        int x = src;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    copy(src: number): void {",
            "        let x: number = src;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesDeepChainedAccess() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void run() {",
            "        int x = first.second().third.fourth;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    run(): void {",
            "        let x: number = first.second().third.fourth;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesInvokableInIfCondition() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void check() {",
            "        if (isValid(run())) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    check(): void {",
            "        if (isValid(run())) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesMemberAccessInWhileCondition() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void loop(Iter it) {",
            "        while (it.hasNext()) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    loop(it: Iter): void {",
            "        while (it.hasNext()) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void parsesNotOperatorInIfCondition() {
        var javaSrc = String.join(System.lineSeparator(),
            "public class Foo {",
            "    void check() {",
            "        if (!isValid(1)) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "export default class Foo {",
            "    check(): void {",
            "        if (!isValid(1)) {",
            "            System.out.println(1);",
            "        }",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
