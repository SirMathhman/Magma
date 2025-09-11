package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class UnionClassFnsTest {

	@Test
	public void unionClassFnsIsCheck() {
		String[] programs = new String[] {
				"class fn Ok() => Ok { } class fn Err() => Err { } type Result = Ok | Err; let a : Result = Ok(); a is Ok",
				"class fn Ok() => Ok { } class fn Err() => Err { } type Result = Ok | Err; let b : Result = Err(); b is Err" };
		for (String program : programs) {
			Result<String, InterpretError> r = new Interpreter().interpret(program);
			if (r instanceof Result.Err) {
				fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r).error());
			}
			assertEquals("true", ((Result.Ok<String, InterpretError>) r).value());
		}
	}
}
