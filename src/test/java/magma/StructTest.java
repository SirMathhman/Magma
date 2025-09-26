package magma;

import magma.api.MagmaError;
import magma.api.Option;
import magma.api.Result;
import magma.api.Tuple;
import magma.compile.CompileError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StructTest {
	private static final String DECL = "intrinsic fn readInt() : I32; ";

	@Test
	public void basicStructDeclarationAndUsage() {
		assertValid("struct Point { x : I32, y : I32 } let p = Point { 3, 4 }; p.x", "", new Tuple<>("", 3));
	}

	@Test
	public void structFieldAccess() {
		assertValid("struct Point { x : I32, y : I32 } let p = Point { 5, 10 }; p.y", "", new Tuple<>("", 10));
	}

	@Test
	public void complexStructUsage() {
		assertValid("struct Rectangle { width : I32, height : I32 } struct Circle { radius : I32 } let rect = Rectangle { 10, 20 }; let circle = Circle { 5 }; rect.width + circle.radius", "", new Tuple<>("", 15));
	}

	@Test
	public void structWithDifferentTypes() {
		assertValid("struct Mixed { flag : Bool, value : I32 } let m = Mixed { true, 42 }; if (m.flag) m.value else 0", "", new Tuple<>("", 42));
	}

	@Test
	public void invalidStructFieldAccess() {
		assertInvalid("struct Point { x : I32, y : I32 } let p = Point { 3, 4 }; p.z");
	}

	@Test
	public void unknownStructType() {
		assertInvalid("let p = UnknownStruct { 1, 2 };");
	}

	@Test
	public void wrongFieldCount() {
		assertInvalid("struct Point { x : I32, y : I32 } let p = Point { 3 };");
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