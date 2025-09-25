package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

	@Test
	public void read() {
		assertOkEquals("intrinsic fn readInt() : I32; readInt()", "5", new Tuple<>("", 5));
	}

	@Test
	public void readAndAdd() {
		assertOkEquals("intrinsic fn readInt() : I32; readInt() + readInt()", "3\r\n4", new Tuple<>("", 7));
	}

	@Test
	public void readMinusRead() {
		assertOkEquals("intrinsic fn readInt() : I32; readInt() - readInt()", "3\r\n2", new Tuple<>("", 1));
	}

	@Test
	public void readNegative() {
		assertOkEquals("intrinsic fn readInt() : I32; readInt()", "-1", new Tuple<>("", -1));
	}

	private void assertOkEquals(String program, String input, Tuple<String, Integer> expected) {
		var res = Runner.run(program, input);
		switch (res) {
			case Result.Ok(var value) -> assertEquals(expected, value);
			case Result.Err(var error) -> fail(error);
		}
	}
}
