package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	private record IntegerTestCase(String name, String type, String cType) {}

	private static Stream<IntegerTestCase> integerTestCases() {
		return Stream.of(new IntegerTestCase("x", "I32", "int32_t"), new IntegerTestCase("a", "I8", "int8_t"),
										 new IntegerTestCase("b", "I16", "int16_t"), new IntegerTestCase("c", "I64", "int64_t"),
										 new IntegerTestCase("d", "U8", "uint8_t"), new IntegerTestCase("e", "U16", "uint16_t"),
										 new IntegerTestCase("f", "U32", "uint32_t"), new IntegerTestCase("g", "U64", "uint64_t"));
	}

	@Test
	void baseCases() {
		assertInvalid("?");
		assertValid("", "");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}

	@ParameterizedTest
	@MethodSource("integerTestCases")
	void letInteger(IntegerTestCase testCase) {
		String in = String.format("let %s : %s = 0;", testCase.name(), testCase.type());
		String out = String.format("#include <stdint.h>\n%s %s = 0;", testCase.cType(), testCase.name());
		assertValid(in, out);
	}

	@Test
	void combinedValidCases() {
		assertValid("let x = 100;", "#include <stdint.h>\nint32_t x = 100;");
		assertValid("let x = 0U8;", "#include <stdint.h>\nuint8_t x = 0;");
		assertValid("let x = 100; let y = x;", "#include <stdint.h>\nint32_t x = 100;\nint32_t y = x;");
		assertValid("let x = 1 ? 2 : 3;", "#include <stdint.h>\nint32_t x = 1 ? 2 : 3;");
		assertValid("let mut x = 100; x = 200;", "#include <stdint.h>\nint32_t x = 100;\nx = 200;");
		assertValid("let mut x = 0U8; x = 1U8;", "#include <stdint.h>\nuint8_t x = 0;\nx = 1;");
		assertValid("let x = 10; let y : *I32 = &x;", "#include <stdint.h>\nint32_t x = 10;\nint32_t* y = &x;");
		assertValid("let x = 10; let y : *I32 = &x; let z : I32 = *y;", "#include <stdint.h>\nint32_t x = 10;\nint32_t* y = &x;\nint32_t z = *y;");
	}

	private void assertValid(String input, String output) {
		assertEquals(output, assertDoesNotThrow(() -> Compiler.compile(input)));
	}

	@Test
	void combinedInvalidCases() {
		assertInvalid("let x : U8 = 0I16;");
		assertInvalid("let x = 100; x = 200;");
		assertInvalid("let mut x = 0U8; x = 1;");
		assertInvalid("let mut x = 0U8; x = 1 ? 2 : 3;");
	}
}