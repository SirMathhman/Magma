package magma;

import magma.api.MagmaError;
import magma.api.Option;
import magma.api.Result;
import magma.api.Tuple;
import magma.compile.CompileError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointerTest {
	private static final String DECL = "intrinsic fn readInt() : I32; ";

	@Test
	public void basicPointerUsage() {
		assertValid("let x = 42; let y = &x; *y", "", new Tuple<>("", 42));
	}

	@Test
	public void pointerWithReadInt() {
		assertValid("let x = readInt(); let y = &x; *y", "25", new Tuple<>("", 25));
	}

	@Test
	public void pointerArithmetic() {
		assertValid("let x = 10; let ptr = &x; (*ptr) + 5", "", new Tuple<>("", 15));
	}

	@Test
	public void multiplePointers() {
		assertValid("let x = 7; let y = 3; let px = &x; let py = &y; (*px) + (*py)", "", new Tuple<>("", 10));
	}

	@Test
	public void dereferenceNonPointer() {
		assertInvalid("let x = 42; *x");
	}

	@Test
	public void pointerTypeInference() {
		// Type inference should work - this should be valid
		assertValid("let x = 42; let y = &x; *y", "", new Tuple<>("", 42));
	}

	private void assertInvalid(String program) {
		String fullProgram = DECL + program;
		Result<Tuple<String, Integer>, RunError> res = Runner.run(fullProgram, "");
		switch (res) {
			case Result.Ok(Tuple<String, Integer> value) -> fail("Expected an error but got Ok: " + value);
			case Result.Err(RunError error) -> {
				Option<MagmaError> causeOpt = error.getCause();
				switch (causeOpt) {
					case Option.Some(MagmaError cause) -> {
						if (!(cause instanceof CompileError))
							fail("Expected a CompileError cause but got: " + cause.display());
					}
					case Option.None() -> fail("Expected a CompileError cause but none was present; RunError=" + error);
				}
			}
		}
	}

	private void assertValid(String program, String input, Tuple<String, Integer> expected) {
		String fullProgram = DECL + program;
		Result<Tuple<String, Integer>, RunError> res = Runner.run(fullProgram, input);
		switch (res) {
			case Result.Ok(Tuple<String, Integer> value) -> assertEquals(expected, value);
			case Result.Err(RunError error) -> fail(error.toString());
		}
	}
}