package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
	private Result<String, InterpreterError> run(String input) {
		Interpreter interp = new Interpreter();
		return interp.interpret(input);
	}

	@Test
	public void interpret_emptyInput_returnsEmptyString() {
		Result<String, InterpreterError> result = run("");
		assertEquals("", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_nonEmpty_returnsErrorResult() {
		Result<String, InterpreterError> result = run("hello");
		assertEquals(true, result.getError().isPresent());
	}

	@Test
	public void interpret_numericInput_returnsSameString() {
		Result<String, InterpreterError> result = run("100");
		assertEquals("100", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_numericPrefix_returnsPrefix() {
		Result<String, InterpreterError> result = run("100U8");
		assertEquals("100", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_simpleAddition_returnsSum() {
		Result<String, InterpreterError> result = run("1 + 2");
		assertEquals("3", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_invalidAddition_withSuffixes_returnsError() {
		Result<String, InterpreterError> result = run("1U8 + 2I32");
		assertEquals(true, result.getError().isPresent());
	}

	@Test
	public void interpret_addition_with_identicalSuffixes_returnsSum() {
		Result<String, InterpreterError> result = run("1I32 + 2I32");
		assertEquals("3", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_invalidAddition_differentIntegerSizes_returnsError() {
		Result<String, InterpreterError> result = run("1I16 + 2I32");
		assertEquals(true, result.getError().isPresent());
	}

	@Test
	public void interpret_invalidAddition_differentSuffixes_returnsError() {
		Result<String, InterpreterError> result = run("1I16 + 2I32");
		assertEquals(true, result.getError().isPresent());
	}

	@Test
	public void interpret_chainedAddition_returnsSum() {
		Result<String, InterpreterError> result = run("1 + 2 + 3");
		assertEquals("6", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_invalidMixedSuffixChainedAddition_returnsError() {
		Result<String, InterpreterError> result = run("1U8 + 2 + 3I32");
		assertEquals(true, result.getError().isPresent());
	}

	@Test
	public void interpret_subtraction_simple_returnsDifference() {
		Result<String, InterpreterError> result = run("10 - 4");
		assertEquals("6", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_mixedSubtractionAddition_leftToRight_returnsSeven() {
		Result<String, InterpreterError> result = run("10 - 4 + 1");
		assertEquals("7", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_simpleMultiplication_returnsProduct() {
		Result<String, InterpreterError> result = run("2 * 3");
		assertEquals("6", result.getValue().orElse("<missing>"));
	}
	
	@Test
	public void interpret_precedence_multiplicationBeforeAddition_returnsSeven() {
		Result<String, InterpreterError> result = run("1 + 2 * 3");
		assertEquals("7", result.getValue().orElse("<missing>"));
	}
}
