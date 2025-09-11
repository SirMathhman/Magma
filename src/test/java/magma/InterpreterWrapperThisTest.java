package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterWrapperThisTest {
	@Test
	public void wrapperThisMethod() {
		Interpreter interp = new Interpreter();
		String source = "fn Wrapper() => {fn get() => 100; this} Wrapper().get()";

		Result<String, InterpretError> res = interp.interpret(source);

        
		assertInstanceOf(Result.Ok.class, res);
		Result.Ok<String, InterpretError> ok = (Result.Ok<String, InterpretError>) res;
		assertEquals("100", ok.value());
	}
}
