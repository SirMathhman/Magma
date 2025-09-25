package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {
	private static final String DECL = "intrinsic fn readInt() : I32; ";

	@Test
	public void read() {
		assertValid("readInt()", "5", new Tuple<>("", 5));
	}

	@Test
	public void readAndAdd() {
		assertValid("readInt() + readInt()", "3\r\n4", new Tuple<>("", 7));
	}

	@Test
	public void readMinusRead() {
		assertValid("readInt() - readInt()", "3\r\n2", new Tuple<>("", 1));
	}

	@Test
	public void readNegative() {
		assertValid("readInt()", "-1", new Tuple<>("", -1));
	}

	@Test
	public void readTripleAdd() {
		assertValid("readInt() + readInt() + readInt()", "1\r\n2\r\n3", new Tuple<>("", 6));
	}

	@Test
	public void readMixedOperators() {
		assertValid("readInt() + readInt() - readInt()", "6\r\n5\r\n3", new Tuple<>("", 8));
	}

	@Test
	public void readMultiply() {
		assertValid("readInt() * readInt()", "6\r\n5", new Tuple<>("", 30));
	}

	@Test
	public void readPrecedence() {
		assertValid("readInt() + readInt() * readInt()", "1\r\n6\r\n5", new Tuple<>("", 31));
	}

	@Test
	public void readLetAssignment() {
		assertValid("let result : I32 = readInt() + readInt() * readInt(); result", "1\r\n6\r\n5", new Tuple<>("", 31));
	}

	@Test
	public void readLetTwoVars() {
		assertValid("let x : I32 = readInt(); let y : I32 = readInt(); x + y", "1\r\n2", new Tuple<>("", 3));
	}

	@Test
	public void duplicateLetNameIsError() {
		assertInvalid("let x : I32 = readInt(); let x : I32 = readInt();");
	}

	@Test
	public void loneConstantLetReturnsZero() {
		assertValid("let x : I32 = 100;", "", new Tuple<>("", 0));
	}

	@Test
	public void compositeLetLiteralReturned() {
		assertValid("let x = 100; x", "", new Tuple<>("", 100));
	}

	@Test
	public void literalTrueReturnsOne() {
		assertValid("true", "", new Tuple<>("", 1));
	}

	@Test
	public void compositeLetBooleanReturned() {
		assertValid("let x = true; x", "", new Tuple<>("", 1));
	}

	@Test
	public void typeMismatchLetIsError() {
		assertInvalid("let y : I32 = 0; let x : Bool = y;");
	}

	@Test
	public void readIntLetsReturnFirst() {
		assertValid("let x = readInt(); let y = readInt(); x", "10\r\n20", new Tuple<>("", 10));
	}

	@Test
	public void readIntEqualsReadInt() {
		assertValid("readInt() == readInt()", "3\r\n3", new Tuple<>("", 1));
	}

	@Test
	public void ifReadIntEqualsThenElse() {
		assertValid("if (readInt() == readInt()) 100 else 20", "3\r\n3", new Tuple<>("", 100));
	}

	@Test
	public void compositeLetIfReturned() {
		assertValid("let result = if (readInt() == readInt()) 100 else 20; result", "3\r\n3", new Tuple<>("", 100));
	}

	@Test
	public void ifNonBooleanIsError() {
		assertInvalid("if (5) 3 else 5");
	}

	@Test
	public void mutLetAssignmentReadInt() {
		assertValid("let mut x = 0; x = readInt(); x", "100", new Tuple<>("", 100));
	}

	@Test
	public void assignToNonMutIsError() {
		assertInvalid("let  x = 0; x = readInt(); x");
	}

	@Test
	public void compoundAddAssignToMut() {
		assertValid("let mut x = 5; x += readInt(); x", "100", new Tuple<>("", 105));
	}

	@Test
	public void compoundAddAssignToNonMutIsError() {
		assertInvalid("let x = 5; x += readInt(); x");
	}

	@Test
	public void whileLoopCounterToLimit() {
		assertValid("let mut counter = 0; let limit = readInt(); while (counter < limit) counter++; counter", "100",
				new Tuple<>("", 100));
	}

	@Test
	public void simpleFunctionReturningReadInt() {
		assertValid("fn get() : I32 => { return readInt(); } get()", "100", new Tuple<>("", 100));
	}

	@Test
	public void duplicateFunctionDeclarationIsError() {
		assertInvalid("fn get() : I32 => { return readInt(); } fn get() : I32 => { return 100; }");
	}

	@Test
	public void oneParamPassThroughFunctionWithReadIntArg() {
		assertValid("fn pass(value : I32) : I32 => { return value; } pass(readInt())", "100",
				new Tuple<>("", 100));
	}

	@Test
	public void twoParamAddFunctionWithReadIntArgs() {
		assertValid("fn add(first : I32, second : I32) : I32 => { return first + second; } add(readInt(), readInt())",
				"100\r\n200", new Tuple<>("", 300));
	}

	@Test
	public void functionWithDuplicateParameterNamesIsError() {
		assertInvalid("fn add(first : I32, first : I32) : I32 => { return first + first; }");
	}

	@Test
	public void mutVarUpdatedByDeclaredFunction() {
		assertValid("let mut x = 0; fn add() : Void => { x += readInt(); } add(); x", "100",
				new Tuple<>("", 100));
	}

	@Test
	public void bracedReadInt() {
		assertValid("{readInt()}", "100", new Tuple<>("", 100));
	}

	@Test
	public void bracedLetReadInt() {
		assertValid("{let x = readInt(); x}", "100", new Tuple<>("", 100));
	}

	private void assertInvalid(String program) {
		String fullProgram = DECL + program;
		var res = Runner.run(fullProgram, "");
		switch (res) {
			case Result.Ok(var value) -> fail("Expected an error but got Ok: " + value);
			case Result.Err(var error) -> assertNotNull(error);
		}
	}

	private void assertValid(String program, String input, Tuple<String, Integer> expected) {
		String fullProgram = DECL + program;
		var res = Runner.run(fullProgram, input);
		switch (res) {
			case Result.Ok(var value) -> assertEquals(expected, value);
			case Result.Err(var error) -> fail(error);
		}
	}
}
