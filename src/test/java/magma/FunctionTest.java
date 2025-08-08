package magma;

import org.junit.jupiter.api.Test;

/**
 * Tests for function declaration and usage in the Magma compiler.
 */
class FunctionTest extends BaseCompilerTest {
	@Test
	void compileFunctionDeclaration() {
		// Test the empty function declaration
		assertValid("fn empty() : Void => {}", "void empty() {}");

		// Test functions with different names
		assertValid("fn hello() : Void => {}", "void hello() {}");
		assertValid("fn calculateSum() : Void => {}", "void calculateSum() {}");
	}

	@Test
	void compileFunctionWithReturnValue() {
		// Test function with return value
		assertValid("fn returnNumber() => {return 100;}", "int returnNumber() {return 100;}");
	}
	
	@Test
	void compileFunctionWithExplicitReturnType() {
		// Test function with U64 return type
		assertValid("fn returnNumber() : U64 => {return 100;}", "uint64_t returnNumber() {return 100;}");
		
		// Test function with other integer types
		assertValid("fn returnI8() : I8 => {return 10;}", "int8_t returnI8() {return 10;}");
		assertValid("fn returnU32() : U32 => {return 42;}", "uint32_t returnU32() {return 42;}");
		
		// Test function with Bool return type
		assertValid("fn returnBool() : Bool => {return true;}", "bool returnBool() {return true;}");
	}
}