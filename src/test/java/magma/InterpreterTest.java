package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {
	@Test
	void empty() {
		Result<String, InterpreterError> r = Interpreter.interpret("");
		assertEquals(new Ok<String, InterpreterError>(""), r);
	}

	@Test
	void integer() {
		Result<String, InterpreterError> r = Interpreter.interpret("1");
		assertEquals(new Ok<String, InterpreterError>("1"), r);
	}

	@Test
	void mixedDigits() {
		Result<String, InterpreterError> r = Interpreter.interpret("1U8");
		assertEquals(new Ok<String, InterpreterError>("1"), r);
	}

	@Test
	void simpleAddition() {
		Result<String, InterpreterError> r = Interpreter.interpret("1 + 2");
		assertEquals(new Ok<String, InterpreterError>("3"), r);
	}

	@Test
	void mixedAdditionOperands() {
		Result<String, InterpreterError> r = Interpreter.interpret("1U8 + 2U8");
		assertEquals(new Ok<String, InterpreterError>("3"), r);
	}

	@Test
	void mismatchedSuffixesAddition() {
		Result<String, InterpreterError> r = Interpreter.interpret("1U8 + 2U16");
		// Expect Err with the original source as message
		assertEquals(new Err<String, InterpreterError>(new InterpreterError("Invalid input", "1U8 + 2U16")), r);
	}
}