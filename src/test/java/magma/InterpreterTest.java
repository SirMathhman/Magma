package magma;

import magma.interpret.InterpretError;
import magma.interpret.Interpreter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	void empty() {
		assertValid("", "");
	}

	@Test
	void undefined() {
		assertInvalid("test");
	}

	@Test
	void numberLiteral() {
		assertValid("5", "5");
	}

	@Test
	void numberLiteralWithTrailing() {
		assertValid("5I32", "5");
	}

	@Test
	void additionSimple() {
		assertValid("2 + 3", "5");
	}

	@Test
	void additionWithTrailingType() {
		assertValid("2 + 3I32", "5");
	}

	@Test
	void subtractionSimple() {
		assertValid("5 - 3", "2");
	}

	@Test
	void multiplicationSimple() {
		assertValid("5 * 3", "15");
	}

	@Test
	void chainedAddition() {
		assertValid("1 + 2 + 3", "6");
	}

	@Test
	void letBinding() {
		assertValid("let x : I32 = 10; x", "10");
	}

	@Test
	void letBindingNoType() {
		assertValid("let x = 10; x", "10");
	}

	@Test
	void letChain() {
		assertValid("let x = 10; let y = x; y", "10");
	}

	@Test
	void letTwoBindings() {
		assertValid("let x = 10; let y = 40; x", "10");
	}

	@Test
	void letOnlyStatement() {
		assertValid("let x = 10;", "");
	}

	@Test
	void letDuplicateBinding() {
		assertInvalid("let x = 0; let x = 0;");
	}

	@Test
	void letTypeMismatch() {
		assertInvalid("let x : U8 = 10I32;");
	}

	@Test
	void letIdentifierTypeMismatch() {
		assertInvalid("let x : I32 = 10; let y : U8 = x;");
	}

	@Test
	void additionMixedUnsignedAndSigned() {
		assertInvalid("2U8 + 3I32");
	}

	@Test
	void mixedSuffixesInChain() {
		assertInvalid("0I8 + 0 + 0U32");
	}

	@Test
	void booleanLiteral() {
		assertValid("true", "true");
	}

	@Test
	void letBoolAssignedNumberShouldBeInvalid() {
		assertInvalid("let x : Bool = 0;");
	}

	@Test
	void functionDeclarationAndCallSimple() {
		assertValid("fn get() : I32 => 100; get()", "100");
	}

	@Test
	void functionDeclarationWithoutTypeAndCall() {
		assertValid("fn get() => 100; get()", "100");
	}

	@Test
	void functionWithParamAndCall() {
		assertValid("fn get(param : I32) => param; get(100)", "100");
	}

	@Test
	void duplicateFunctionDeclarationShouldBeInvalid() {
		assertInvalid("fn get() => 0; fn get() => 0;");
	}

	private static void assertValid(String input, String expected) {
		switch (new Interpreter().interpret(input)) {
			case Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}

	private static void assertInvalid(String input) {
		switch (new Interpreter().interpret(input)) {
			case Err<String, InterpretError>(InterpretError error) -> assertNotNull(error);
			case Ok<String, InterpretError>(String value) -> fail("Expected error but got: " + value);
		}
	}
}
