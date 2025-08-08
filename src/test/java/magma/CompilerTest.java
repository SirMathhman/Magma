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
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
		assertEquals("", assertDoesNotThrow(() -> Compiler.compile("")));
	}

	@ParameterizedTest
	@MethodSource("integerTestCases")
	void letInteger(IntegerTestCase testCase) {
		String in = String.format("let %s : %s = 0;", testCase.name(), testCase.type());
		String out = String.format("#include <stdint.h>\n%s %s = 0;", testCase.cType(), testCase.name());
		assertEquals(out, assertDoesNotThrow(() -> Compiler.compile(in)));
	}

	@Test
	void combinedValidCases() {
		assertEquals("#include <stdint.h>\nint32_t x = 100;", assertDoesNotThrow(() -> Compiler.compile("let x = 100;")));
		assertEquals("#include <stdint.h>\nuint8_t x = 0;", assertDoesNotThrow(() -> Compiler.compile("let x = 0U8;")));
		assertEquals("#include <stdint.h>\nint32_t x = 100;\nint32_t y = x;",
								 assertDoesNotThrow(() -> Compiler.compile("let x = 100; let y = x;")));
		assertEquals("#include <stdint.h>\nint32_t x = 1 ? 2 : 3;",
								 assertDoesNotThrow(() -> Compiler.compile("let x = 1 ? 2 : 3;")));
		assertEquals("#include <stdint.h>\nint32_t x = 100;\nx = 200;",
								 assertDoesNotThrow(() -> Compiler.compile("let mut x = 100; x = 200;")));
		assertEquals("#include <stdint.h>\nuint8_t x = 0;\nx = 1;",
								 assertDoesNotThrow(() -> Compiler.compile("let mut x = 0U8; x = 1U8;")));
	}

	@Test
	void combinedInvalidCases() {
		assertThrows(CompileException.class, () -> Compiler.compile("let x : U8 = 0I16;"));
		assertThrows(CompileException.class, () -> Compiler.compile("let x = 100; x = 200;"));
		assertThrows(CompileException.class, () -> Compiler.compile("let mut x = 0U8; x = 1;"));
		assertThrows(CompileException.class, () -> Compiler.compile("let mut x = 0U8; x = 1 ? 2 : 3;"));
	}
}