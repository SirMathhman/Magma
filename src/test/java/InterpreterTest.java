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
	public void invalid() {
		assertThrows(InterpretException.class, () -> Interpreter.interpret("test"));
	}
}
