package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Make the tests pass. Do not add more tests.
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
	@ValueSource(strings = {"I16, I32", "I16"})
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
		assertRun("fn test() => {return " + value + ";} test()", value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"100", "200"})
	void testReturns(String value) {
		assertRun("fn test() => {" + value + "} test()", value);
	}

	@Test
	void letInFunction() {
		assertRun("fn test() => {let x = 10; x} test()", "10");
	}

	@ParameterizedTest
	@ValueSource(strings = {"first", "second"})
	void call(String name) {
		assertRun("fn " + name + "() => {x} " + name + "()", "10");
	}

	@ParameterizedTest
	@ValueSource(strings = {"100", "200"})
	void testClass(String value) {
		assertRun("class fn Wrapper() => {let x = " + value + ";} Wrapper().x", value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"100", "200"})
	void testMethod(String value) {
		assertRun("class fn Wrapper() => {fn test() => " + value + ";} Wrapper().test()", value);
	}

	private void assertRun(String input, String output) {
		assertEquals(output, Main.run(input));
	}
}
