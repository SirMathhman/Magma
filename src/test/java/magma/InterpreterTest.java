package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
	@Test
	void interpretReturnsThreeForIfTrueElseFive() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("3", interpreter.interpret("if (true) 3 else 5"));
	}

	@Test
	void interpretReturnsEmptyStringForEmptyInput() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("", interpreter.interpret(""));
	}

	@Test
	void interpretThrowsExceptionForNonEmptyInput() {
		Interpreter interpreter = new Interpreter();
		assertThrows(InterpretException.class, () -> interpreter.interpret("Hello"));
	}

	@Test
	void interpretReturnsFiveForFiveInput() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("5", interpreter.interpret("5"));
	}

	@Test
	void interpretReturnsFiveForLetXEqualsFiveX() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("5", interpreter.interpret("let x = 5; x"));
	}

	@Test
	void interpretReturnsTenForLetMutXAssignment() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("10", interpreter.interpret("let mut x = 0; x = 10; x"));
	}

	@Test
	void interpretReturnsOneForLetMutXPlusEqualsOne() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("1", interpreter.interpret("let mut x = 0; x += 1; x"));
	}

	@Test
	void interpretReturnsThreeForWhileLoopCounter() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("3", interpreter.interpret("let mut counter = 0; while (counter < 3) counter += 1; counter"));
	}

	@Test
	void interpretReturnsTrueForTrueInput() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("true", interpreter.interpret("true"));
	}

	@Test
	void interpretReturnsTrueForThreeLessThanFive() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("true", interpreter.interpret("3 < 5"));
	}

	@Test
	void interpretReturnsOneHundredForFunctionCall() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("100", interpreter.interpret("fn get() : I32 => {return 100;} get()"));
	}

	@Test
	void interpretReturnsOneHundredForArrayIndex() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("100", interpreter.interpret("[100][0]"));
	}

	@Test
	void interpretReturnsOneHundredForStructField() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("100", interpreter.interpret("struct Wrapper { field : I32 } Wrapper { 100 }.field"));
	}

	@Test
	void interpretReturnsEmptyForStructDeclarationOnly() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("", interpreter.interpret("struct Wrapper { field : I32 }"));
	}

	@Test
	void interpretReturnsOneHundredForGenericStructField() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("100", interpreter.interpret("struct Wrapper<T> { field : T } Wrapper<I32>{ 100 }.field"));
	}

	@Test
	void interpretReturnsOneHundredForPointerDeref() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("100", interpreter.interpret("let x = 100; *&x"));
	}
}