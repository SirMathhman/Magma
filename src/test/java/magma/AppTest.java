package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest {
	@Test
	public void testInterpret() {
		String in = "echo";
		assertEquals(in, App.interpret(in));
	}

	@Test
	public void testInterpretNumericSuffix() {
		assertEquals("100", App.interpret("100U8"));
	}

	@Test
	public void testInterpretNegativeU8Throws() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("-1U8"));
	}

	@Test
	public void testInterpretU8OverflowThrows() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("256U8"));
	}

	@Test
	public void testInterpretU16() {
		assertEquals("65535", App.interpret("65535U16"));
	}

	@Test
	public void testInterpretU32() {
		assertEquals("4294967295", App.interpret("4294967295U32"));
	}

	@Test
	public void testInterpretU64() {
		assertEquals("18446744073709551615", App.interpret("18446744073709551615U64"));
	}

	@Test
	public void testInterpretI16Overflow() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("32768I16"));
	}

	@Test
	public void testInterpretI32Overflow() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("2147483648I32"));
	}

	@Test
	public void testInterpretI64Overflow() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("9223372036854775808I64"));
	}

	@Test
	public void testInterpretAddition() {
		assertEquals("3", App.interpret("1 + 2"));
		assertEquals("3", App.interpret("1+2"));
	}

	@Test
	public void testInterpretAdditionMixed() {
		assertEquals("3", App.interpret("1 + 2U8"));
		assertEquals("3", App.interpret("2U8 + 1"));
		assertEquals("3", App.interpret("2U8+1U8"));
	}

	@Test
	public void testInterpretAdditionDifferentTypedOperandsThrows() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("1U8 + 2U16"));
		assertThrows(IllegalArgumentException.class, () -> App.interpret("1I16 + 2U16"));
		assertEquals("3", App.interpret("1I16 + 2I16"));
		assertEquals("3", App.interpret("1U16 + 2U16"));
	}

	@Test
	public void testInterpretAdditionMixedOverflowThrows() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("255U8 + 1"));
		assertThrows(IllegalArgumentException.class, () -> App.interpret("1 + 255U8"));
	}
}
