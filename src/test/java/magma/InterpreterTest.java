package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

	@ParameterizedTest
	@MethodSource("cases")
	void interpretCases(String input, String expected) {
		assertInterpretsTo(input, expected);
	}

	private static Stream<Arguments> cases() {
		String[][] pairs = new String[][] {
				{ "", "" },
				{ "0", "0" },
				{ "3 + 5", "8" },
				{ "let x : I32 = 3; x", "3" },
				{ "let x : I32 = 10; let y : I32 = 20; x", "10" },
				{ "let mut x : I32 = 0; x = 3; x", "3" },
				{ "true", "true" },
				{ "if (true) 3 else 5", "3" },
				{ "let mut x = 2; x++; x", "3" },
				{ "let mut x = 2; x += 3; x", "5" },
				{ "3 < 2", "false" },
				{ "let mut sum = 0; let mut index = 0; while (index < 4) { sum += index; index++; } sum", "6" },
				{ "let mut sum = 0; for (let mut index = 0; index < 4; index++) { sum += index; } sum", "6" },
				{ "struct Wrapper { field : I32 } let value = Wrapper { 6 }; value.field", "6" },
				{ "fn get() => 100; get()", "100" }
		};
		return java.util.Arrays.stream(pairs).map(p -> Arguments.of(p[0], p[1]));
	}

	private static void assertInterpretsTo(String input, String expected) {
		Interpreter interpreter = new Interpreter();
		Result<String, InterpretError> result = interpreter.interpret(input);
		assertTrue(result instanceof Ok);
		if (result instanceof Ok ok) {
			assertEquals(expected, ok.getValue());
		} else {
			fail("expected Ok result");
		}
	}

}
