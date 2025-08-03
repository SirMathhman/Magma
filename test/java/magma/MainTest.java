package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
	@ParameterizedTest
	@ValueSource(strings = {"-10", "-1", "0", "1", "10"})
	void digit(String input) {
		assertRun(input, input);
	}

	@Test
	void add() {
		assertRun("1 + 2", "3");
	}

	@Test
	void addTwice() {
		assertRun("1 + 2 + 3", "6");
	}

	@Test
	void addThrice() {
		assertRun("1 + 2 + 3 + 4", "10");
	}

	@Test
	void parentheses() {
		assertRun("(1 + 2) * 3", "9");
	}

	@Test
	void subtract() {
		assertRun("2 - 1", "1");
	}

	@Test
	void multiply() {
		assertRun("4 * 5", "20");
	}

	@Test
	void divide() {
		assertRun("10 / 2", "5");
	}

	@Test
	void whitespace() {
		assertRun("2+3+4", "9");
	}

	@ParameterizedTest
	@ValueSource(strings = {"x", "y", "z"})
	void let(String name) {
		assertRun("let " + name + " = 10; " + name, "10");
	}

 @ParameterizedTest
	@ValueSource(strings = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"})
	void letWithType(String type) {
		assertRun("let test : " + type + " = 10; test", "10");
	}

	@Test
	void empty() {
		assertRun("", "");
	}

	@Test
	void block() {
		assertRun("{}", "");
	}

	@Test
	void blockLet() {
		assertRun(" { let x = 10; x }", "10");
	}

	@Test
	void testFalse() {
		assertRun("false", "0");
	}

	@Test
	void testTrue() {
		assertRun("true", "1");
	}

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

	@Test
	void complexArithmetic() {
		assertRun("2 * 3 + 4 * 5", "26");
	}

	@Test
	void complexArithmeticWithParentheses() {
		assertRun("2 * (3 + 4) * 5", "70");
	}

	@Test
	void nestedParentheses() {
		assertRun("((1 + 2) * (3 + 4))", "21");
	}

	@Test
	void nestedBlocks() {
		assertRun("{ { let x = 5; x } }", "5");
	}

	@Test
	void multipleVariables() {
		assertRun("let x = 5; let y = 10; x + y", "15");
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
	void ifStatement() {
		assertRun("fn test(x: I32) => { if (x > 5) { 10 } else { 5 } }\ntest(10)", "10");
	}

	@Test
	void ifStatementFalseCondition() {
		assertRun("fn test(x: I32) => { if (x > 5) { 10 } else { 5 } }\ntest(3)", "5");
	}

	private void assertRun(String input, String output) {
		assertEquals(output, Main.run(input));
	}
}
