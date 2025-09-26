package magma;

import magma.api.MagmaError;
import magma.api.Option;
import magma.api.Result;
import magma.api.Tuple;
import magma.compile.CompileError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionTypeTest {

	@Test
	public void testSimpleFunctionDeclaration() {
		String program = """
				fn add(first : I32, second : I32) => first + second;
				add(1, 2)
				""";

		assertValid(program, "", new Tuple<>("", 3));
	}

	@Test
	public void testFunctionTypeAnnotation() {
		String program = """
				fn add(first : I32, second : I32) => first + second;
				let addAlias : (I32, I32) => I32 = add;
				addAlias(1, 2)
				""";

		assertValid(program, "", new Tuple<>("", 3));
	}

	@Test
	public void testFunctionTypeAssignmentAndCall() {
		String program = """
				fn add(first : I32, second : I32) => first + second;
				let addAlias : (I32, I32) => I32 = add;
				let result = addAlias(5, 3);
				result
				""";

		assertValid(program, "", new Tuple<>("", 8));
	}

	@Test
	public void testFunctionCallThroughVariable() {
		String program = """
				fn multiply(a : I32, b : I32) => a * b;
				let op : (I32, I32) => I32 = multiply;
				op(4, 5)
				""";

		assertValid(program, "", new Tuple<>("", 20));
	}

	@Test
	public void testTypeMismatchError() {
		String program = """
				fn add(first : I32, second : I32) => first + second;
				let wrongAlias : (I32) => I32 = add;
				wrongAlias(5)
				""";

		assertInvalid(program);
	}

	private void assertInvalid(String program) {
		Result<Tuple<String, Integer>, RunError> res = Runner.run(program, "");
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
		Result<Tuple<String, Integer>, RunError> res = Runner.run(program, input);
		switch (res) {
			case Result.Ok(Tuple<String, Integer> value) -> assertEquals(expected, value);
			case Result.Err(RunError error) -> fail(error.toString());
		}
	}
}