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
	void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
	}

	@Test
	void valid() {
		assertValid("", "");
	}

	@ParameterizedTest
	@MethodSource("integerTestCases")
	void letInteger(IntegerTestCase testCase) {
		assertValid(String.format("let %s : %s = 0;", testCase.name(), testCase.type()),
					String.format("#include <stdint.h>\n%s %s = 0;", testCase.cType(), testCase.name()));
	}

	@Test
	void letDefaultI32() {
		assertValid("let x = 100;", "#include <stdint.h>\nint32_t x = 100;");
	}

	@Test
	void letTypedLiteralSuffix() {
		// Two spaces after '=' should be tolerated, and type suffix should define C type
		assertValid("let x = 0U8;", "#include <stdint.h>\nuint8_t x = 0;");
	}

	@Test
	void mismatchedDeclaredAndLiteralTypeShouldFail() {
		assertThrows(CompileException.class, () -> Compiler.compile("let x : U8 = 0I16;"));
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}
}