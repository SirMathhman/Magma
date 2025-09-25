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

	private void assertValid(String program, String input, Tuple<String, Integer> expected) {
		String fullProgram = DECL + program;
		var res = Runner.run(fullProgram, input);
		switch (res) {
			case Result.Ok(var value) -> assertEquals(expected, value);
			case Result.Err(var error) -> fail(error);
		}
	}
}
