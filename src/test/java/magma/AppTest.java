package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest {
	@BeforeEach
	public void setUp() {
		App.clearVariables();
	}

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

	@Test
	public void testInterpretSubtraction() {
		assertEquals("1", App.interpret("3 - 2"));
		assertEquals("1", App.interpret("3-2"));
	}

	@Test
	public void testInterpretSubtractionMixed() {
		assertEquals("1", App.interpret("3U8 - 2"));
		assertEquals("1", App.interpret("3 - 2U8"));
		assertEquals("1", App.interpret("3U8 - 2U8"));
	}

	@Test
	public void testInterpretSubtractionUnderflow() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("1U8 - 2"));
		assertThrows(IllegalArgumentException.class, () -> App.interpret("0I16 - 33000"));
	}

	@Test
	public void testInterpretChainedAddition() {
		assertEquals("6", App.interpret("1 + 2 + 3"));
		assertEquals("10", App.interpret("1+2+3+4"));
		assertEquals("6", App.interpret("1U8 + 2U8 + 3U8"));
	}

	@Test
	public void testInterpretMixedAdditionSubtraction() {
		assertEquals("5", App.interpret("3 + 4 - 2"));
		assertEquals("0", App.interpret("5 - 2 - 3"));
		assertEquals("6", App.interpret("1 + 2 - 3 + 6"));
	}

	@Test
	public void testInterpretMultiplication() {
		assertEquals("6", App.interpret("3 * 2"));
		assertEquals("6", App.interpret("3*2"));
	}

	@Test
	public void testInterpretMultiplicationMixed() {
		assertEquals("6", App.interpret("3 * 2U8"));
		assertEquals("6", App.interpret("2U8 * 3"));
		assertEquals("6", App.interpret("2U8 * 3U8"));
	}

	@Test
	public void testInterpretMultiplicationOverflow() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("200U8 * 2"));
	}

	@Test
	public void testInterpretChainedMultiplication() {
		assertEquals("24", App.interpret("2 * 3 * 4"));
	}

	@Test
	public void testInterpretDivisionByZero() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("4 / 0"));
	}

	@Test
	public void testInterpretParentheses() {
		assertEquals("1", App.interpret("(1)"));
		assertEquals("5", App.interpret("(1 + 2 + 2)"));
	}

	@Test
	public void testInterpretParenthesesPrecedence() {
		assertEquals("9", App.interpret("(1 + 2) * 3"));
		assertEquals("7", App.interpret("1 + 2 * 3"));
	}

	@Test
	public void testInterpretParenthesesDivisionByZero() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("4 / (2 - 2)"));
	}

	@Test
	public void testInterpretLetBinding() {
		assertEquals("", App.interpret("let x : U8 = 100;"));
		assertEquals("", App.interpret("let y : I16 = -50;"));
	}

	@Test
	public void testInterpretLetBindingInvalid() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("let x : U8 = 256;"));
		assertThrows(IllegalArgumentException.class, () -> App.interpret("let x : I16 = -33000;"));
	}

	@Test
	public void testInterpretVariableReference() {
		App.interpret("let x : U8 = 100;");
		assertEquals("100", App.interpret("x"));
		App.interpret("let y : I16 = -50;");
		assertEquals("-50", App.interpret("y"));
	}

	@Test
	public void testInterpretVariableInExpression() {
		App.interpret("let x : U8 = 100;");
		assertEquals("150", App.interpret("x + 50"));
		assertEquals("50", App.interpret("x - 50"));
	}

	@Test
	public void testInterpretUntypedLetBinding() {
		assertEquals("", App.interpret("let x = 100;"));
		assertEquals("100", App.interpret("x"));
	}

	@Test
	public void testInterpretUntypedLetBindingInExpression() {
		App.interpret("let x = 100;");
		assertEquals("150", App.interpret("x + 50"));
		App.interpret("let y = -50;");
		assertEquals("-50", App.interpret("y"));
	}

	@Test
	public void testInterpretDuplicateVariableThrows() {
		App.interpret("let x = 100;");
		assertThrows(IllegalArgumentException.class, () -> App.interpret("let x = 100;"));
	}

	@Test
	public void testInterpretDuplicateVariableDifferentTypeThrows() {
		App.interpret("let x : U8 = 100;");
		assertThrows(IllegalArgumentException.class, () -> App.interpret("let x : U16 = 100;"));
	}

	@Test
	public void testInterpretVariableDeclaration() {
		assertEquals("", App.interpret("let x : I32;"));
		assertEquals("", App.interpret("x = 100;"));
		assertEquals("100", App.interpret("x"));
	}

	@Test
	public void testInterpretVariableAssignment() {
		App.interpret("let x : U8;");
		assertEquals("", App.interpret("x = 50;"));
		assertEquals("50", App.interpret("x"));
		assertEquals("", App.interpret("x = 100;"));
		assertEquals("100", App.interpret("x"));
	}

	@Test
	public void testInterpretVariableAssignmentUntyped() {
		App.interpret("let y;");
		assertEquals("", App.interpret("y = 42;"));
		assertEquals("42", App.interpret("y"));
	}

	@Test
	public void testInterpretVariableAssignmentInExpression() {
		App.interpret("let x : I32;");
		App.interpret("x = 100;");
		assertEquals("150", App.interpret("x + 50"));
	}

	@Test
	public void testInterpretVariableAssignmentTypeValidation() {
		App.interpret("let x : U8;");
		assertThrows(IllegalArgumentException.class, () -> App.interpret("x = 256;"));
	}

	@Test
	public void testInterpretUninitializedVariableThrows() {
		App.interpret("let x : I32;");
		assertThrows(IllegalArgumentException.class, () -> App.interpret("x"));
	}

	@Test
	public void testInterpretAssignmentToUndefinedVariableThrows() {
		assertThrows(IllegalArgumentException.class, () -> App.interpret("y = 100;"));
	}

	@Test
	public void testInterpretMaxU8() {
		assertEquals("255U8", App.interpret("max<U8>()"));
	}

	@Test
	public void testInterpretMaxU16() {
		assertEquals("65535U16", App.interpret("max<U16>()"));
	}

	@Test
	public void testInterpretMaxU32() {
		assertEquals("4294967295U32", App.interpret("max<U32>()"));
	}

	@Test
	public void testInterpretMaxU64() {
		assertEquals("18446744073709551615U64", App.interpret("max<U64>()"));
	}

	@Test
	public void testInterpretMaxI8() {
		assertEquals("127I8", App.interpret("max<I8>()"));
	}

	@Test
	public void testInterpretMaxI16() {
		assertEquals("32767I16", App.interpret("max<I16>()"));
	}

	@Test
	public void testInterpretMaxI32() {
		assertEquals("2147483647I32", App.interpret("max<I32>()"));
	}

	@Test
	public void testInterpretMaxI64() {
		assertEquals("9223372036854775807I64", App.interpret("max<I64>()"));
	}
}
