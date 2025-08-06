import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the Main class.
 */
public class MainTest {
	/**
	 * Test the basic variable declaration with I32 type.
	 */
	@Test
	public void let() {
		// Test case: Magma variable declaration with I32 type
		assertEquals("int32_t value = 0;", Main.compile("let value : I32 = 0;"));
	}
	
	/**
	 * Test that declaration names are correctly preserved during compilation.
	 */
	@Test
	public void testDeclarationNames() {
		// Test with different variable names
		assertEquals("int32_t counter = 0;", Main.compile("let counter : I32 = 0;"));
		assertEquals("int32_t x = 0;", Main.compile("let x : I32 = 0;"));
		assertEquals("int32_t myVariable = 0;", Main.compile("let myVariable : I32 = 0;"));
		assertEquals("int32_t _temp = 0;", Main.compile("let _temp : I32 = 0;"));
		assertEquals("int32_t camelCaseVar = 0;", Main.compile("let camelCaseVar : I32 = 0;"));
	}
	
	/**
	 * Test all integer types (I8, I16, I32, I64, U8, U16, U32, U64).
	 */
	@Test
	public void testAllIntegerTypes() {
		// Test signed integer types
		assertEquals("int8_t i8Var = 42;", Main.compile("let i8Var : I8 = 42;"));
		assertEquals("int16_t i16Var = 1000;", Main.compile("let i16Var : I16 = 1000;"));
		assertEquals("int32_t i32Var = 100000;", Main.compile("let i32Var : I32 = 100000;"));
		assertEquals("int64_t i64Var = 9223372036854775807;", Main.compile("let i64Var : I64 = 9223372036854775807;"));
		
		// Test unsigned integer types
		assertEquals("uint8_t u8Var = 255;", Main.compile("let u8Var : U8 = 255;"));
		assertEquals("uint16_t u16Var = 65535;", Main.compile("let u16Var : U16 = 65535;"));
		assertEquals("uint32_t u32Var = 4294967295;", Main.compile("let u32Var : U32 = 4294967295;"));
		assertEquals("uint64_t u64Var = 18446744073709551615;", Main.compile("let u64Var : U64 = 18446744073709551615;"));
		
		// Test with different values
		assertEquals("int8_t negativeI8 = -128;", Main.compile("let negativeI8 : I8 = -128;"));
		assertEquals("uint32_t zeroU32 = 0;", Main.compile("let zeroU32 : U32 = 0;"));
	}
	
	/**
	 * Test edge cases for integer types.
	 */
	@Test
	public void testEdgeCases() {
		// Test minimum values for signed types
		assertEquals("int8_t minI8 = -128;", Main.compile("let minI8 : I8 = -128;"));
		assertEquals("int16_t minI16 = -32768;", Main.compile("let minI16 : I16 = -32768;"));
		assertEquals("int32_t minI32 = -2147483648;", Main.compile("let minI32 : I32 = -2147483648;"));
		assertEquals("int64_t minI64 = -9223372036854775808;", Main.compile("let minI64 : I64 = -9223372036854775808;"));
		
		// Test maximum values for signed types
		assertEquals("int8_t maxI8 = 127;", Main.compile("let maxI8 : I8 = 127;"));
		assertEquals("int16_t maxI16 = 32767;", Main.compile("let maxI16 : I16 = 32767;"));
		assertEquals("int32_t maxI32 = 2147483647;", Main.compile("let maxI32 : I32 = 2147483647;"));
		assertEquals("int64_t maxI64 = 9223372036854775807;", Main.compile("let maxI64 : I64 = 9223372036854775807;"));
		
		// Test minimum values for unsigned types (always 0)
		assertEquals("uint8_t minU8 = 0;", Main.compile("let minU8 : U8 = 0;"));
		assertEquals("uint16_t minU16 = 0;", Main.compile("let minU16 : U16 = 0;"));
		assertEquals("uint32_t minU32 = 0;", Main.compile("let minU32 : U32 = 0;"));
		assertEquals("uint64_t minU64 = 0;", Main.compile("let minU64 : U64 = 0;"));
		
		// Test maximum values for unsigned types
		assertEquals("uint8_t maxU8 = 255;", Main.compile("let maxU8 : U8 = 255;"));
		assertEquals("uint16_t maxU16 = 65535;", Main.compile("let maxU16 : U16 = 65535;"));
		assertEquals("uint32_t maxU32 = 4294967295;", Main.compile("let maxU32 : U32 = 4294967295;"));
		assertEquals("uint64_t maxU64 = 18446744073709551615;", Main.compile("let maxU64 : U64 = 18446744073709551615;"));
	}
	
	/**
	 * Test Bool type with true and false values.
	 */
	@Test
	public void testBoolType() {
		// Test Bool type with true value
		assertEquals("bool isActive = true;", Main.compile("let isActive : Bool = true;"));
		
		// Test Bool type with false value
		assertEquals("bool isComplete = false;", Main.compile("let isComplete : Bool = false;"));
		
		// Test Bool type with variable names
		assertEquals("bool flag = true;", Main.compile("let flag : Bool = true;"));
		assertEquals("bool enabled = false;", Main.compile("let enabled : Bool = false;"));
		
		// Test Bool type with whitespace in values
		assertEquals("bool hasSpace = true;", Main.compile("let hasSpace : Bool = true ;"));
		assertEquals("bool hasMoreSpace = false;", Main.compile("let hasMoreSpace : Bool = false  ;"));
	}
	
	/**
	 * Test whitespace variations within variable declarations.
	 */
	@Test
	public void testWhitespaceInVariableDeclarations() {
		// Test extra spaces around variable name
		assertEquals("int32_t value = 42;", Main.compile("let  value  : I32 = 42;"));
		
		// Test extra spaces around type declaration
		assertEquals("int32_t count = 10;", Main.compile("let count  :  I32  = 10;"));
		
		// Test extra spaces around assignment
		assertEquals("int32_t total = 100;", Main.compile("let total : I32  =  100;"));
		
		// Test extra spaces everywhere
		assertEquals("int32_t sum = 50;", Main.compile("let  sum  :  I32  =  50  ;"));
		
		// Test tabs instead of spaces
		assertEquals("int32_t tabVar = 5;", Main.compile("let\ttabVar\t:\tI32\t=\t5;"));
		
		// Test mixed whitespace
		assertEquals("int32_t mixed = 25;", Main.compile("let \t mixed \t : \t I32 \t = \t 25 \t ;"));
		
		// Test with Bool type
		assertEquals("bool flag = true;", Main.compile("let  flag  :  Bool  =  true  ;"));
		
		// Test with other integer types
		assertEquals("int8_t small = 8;", Main.compile("let  small  :  I8  =  8  ;"));
		assertEquals("uint64_t big = 1000000;", Main.compile("let  big  :  U64  =  1000000  ;"));
	}
}