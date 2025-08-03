package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.TestUtils.assertRun;

/**
 * Tests for classes and methods in Magma.
 */
public class ClassesTest {
    @ParameterizedTest
    @ValueSource(strings = {"100", "200"})
    void fieldValue(String value) {
        assertRun("class fn Wrapper() => {let x = " + value + ";}\nWrapper().x", value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void className(String name) {
        assertRun("class fn " + name + "() => {fn test() => 100;}\n" + name + "().test()", "100");
    }

    @ParameterizedTest
    @ValueSource(strings = {"100", "200"})
    void testMethod(String value) {
        assertRun("class fn Wrapper() => {fn test() => " + value + ";}\nWrapper().test()", value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void classParameterName(String name) {
        assertRun("class fn Wrapper(" + name + ": I32) => {}\nWrapper(10)." + name, "10");
    }

    @ParameterizedTest
    @ValueSource(strings = {"100", "200"})
    void classParameterValue(String value) {
        assertRun("class fn Wrapper(x: I32) => {}\nWrapper(" + value + ").x", value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"})
    void classParameterWithDifferentTypes(String type) {
        assertRun("class fn Wrapper(x: " + type + ") => {}\nWrapper(10).x", "10");
    }

    @ParameterizedTest
    @ValueSource(strings = {"10", "20"})
    void classMethodWithParameter(String value) {
        assertRun("class fn Wrapper() => { fn process(a: I32) => { a * 2 } }\nWrapper().process(" + value + ")", String.valueOf(Integer.parseInt(value) * 2));
    }

    @Test
    void classMethodWithMultipleParameters() {
        assertRun("class fn Calculator() => { fn add(a: I32, b: I32) => { a + b } }\nCalculator().add(3, 4)", "7");
    }

    @Test
    void nestedClassMethodsWithNumbers() {
        assertRun("class fn Outer() => { fn createInner() => { class fn Inner() => { fn value() => 42; } Inner() } }\nOuter().createInner().value()", "42");
    }
}