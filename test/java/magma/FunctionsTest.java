package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.TestUtils.assertRun;

/**
 * Tests for functions and function calls in Magma.
 */
public class FunctionsTest {
    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void testFunction(String name) {
        assertRun("fn " + name + "() => {}", "");
    }

    @ParameterizedTest
    @ValueSource(strings = {"100", "200"})
    void testReturnsKeyword(String value) {
        assertRun("fn test() => {return " + value + ";}\ntest()", value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"100", "200"})
    void testReturns(String value) {
        assertRun("fn test() => {" + value + "}\ntest()", value);
    }

    @Test
    void letInFunction() {
        assertRun("fn test() => {let x = 10; x}\ntest()", "10");
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void call(String name) {
        assertRun("fn " + name + "() => {x}\n" + name + "()", "10");
    }

    @ParameterizedTest
    @ValueSource(strings = {"10", "20"})
    void functionWithParameter(String value) {
        assertRun("fn test(a: I32) => { a }\ntest(" + value + ")", value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"})
    void functionWithDifferentTypes(String type) {
        assertRun("fn test(a: " + type + ") => { a }\ntest(10)", "10");
    }

    @Test
    void functionWithMultipleParameters() {
        assertRun("fn add(a: I32, b: I32) => { a + b }\nadd(5, 7)", "12");
    }

    @Test
    void nestedFunctionCalls() {
        assertRun("fn double(x: I32) => { x * 2 }\nfn triple(x: I32) => { x * 3 }\ndouble(triple(2))", "12");
    }

    @Test
    void nestedFunctionCallsWithNumbers() {
        assertRun("fn add(a: I32, b: I32) => { a + b }\nfn multiply(a: I32, b: I32) => { a * b }\nadd(multiply(2, 3), multiply(4, 5))", "26");
    }
}