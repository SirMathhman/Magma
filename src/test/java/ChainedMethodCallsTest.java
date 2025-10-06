import magma.Compiler;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * End-to-end test for chained method calls.
 * Tests parsing, deserialization, and validation of method call chains
 * like builder.withName("test").withAge(25).build()
 */
public class ChainedMethodCallsTest {

	@Test
	public void testSimpleChainedMethodCalls() {
		// Test a simple chained method call: obj.foo().bar()
		String input = """
				package test;
				
				public class ChainExample {
					public void example() {
						obj.foo().bar();
					}
				}
				""";

		final Result<String, CompileError> compile = Compiler.compile(input);
		switch (compile) {
			case Err<String, CompileError> v -> fail(v.error().display());
			case Ok<String, CompileError> v -> assertNotNull(v.value());
		}
	}
}

