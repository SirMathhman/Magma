import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void valid() throws InterpretException {
		String input = "5";
		String result = Interpreter.interpret(input);
		assertEquals("5", result);
	}

	@Test
	public void additionExpression() throws InterpretException {
		String input = "3 + 5";
		String result = Interpreter.interpret(input);
		assertEquals("8", result);
	}

	@Test
	public void subtractionExpression() throws InterpretException {
		String input = "10 - 4";
		String result = Interpreter.interpret(input);
		assertEquals("6", result);
	}

	@Test
	public void multiplicationExpression() throws InterpretException {
		String input = "7 * 6";
		String result = Interpreter.interpret(input);
		assertEquals("42", result);
	}

	@Test
	public void booleanTrue() throws InterpretException {
		String input = "true";
		String result = Interpreter.interpret(input);
		assertEquals("true", result);
	}

	@Test
	public void booleanFalse() throws InterpretException {
		String input = "false";
		String result = Interpreter.interpret(input);
		assertEquals("false", result);
	}

	@Test
	public void nestedAddition() throws InterpretException {
		String input = "1 + (2 + 3)";
		String result = Interpreter.interpret(input);
		assertEquals("6", result);
	}

	@Test
	public void chainedAddition() throws InterpretException {
		String input = "1 + 2 + 3 + 4";
		String result = Interpreter.interpret(input);
		assertEquals("10", result);
	}

	@Test
	public void multiplicationBeforeAddition() throws InterpretException {
		String input = "2 + 3 * 4";
		String result = Interpreter.interpret(input);
		assertEquals("14", result);
	}

	@Test
	public void parenthesesOverridePrecedence() throws InterpretException {
		String input = "(2 + 3) * 4";
		String result = Interpreter.interpret(input);
		assertEquals("20", result);
	}

	@Test
	public void subtractionAndMultiplication() throws InterpretException {
		String input = "10 - 2 * 3";
		String result = Interpreter.interpret(input);
		assertEquals("4", result);
	}

	@Test
	public void multiplicationAndAddition() throws InterpretException {
		String input = "2 * 3 + 4";
		String result = Interpreter.interpret(input);
		assertEquals("10", result);
	}

	@Test
	public void invalid() {
		assertThrows(InterpretException.class, () -> Interpreter.interpret("test"));
	}
}
