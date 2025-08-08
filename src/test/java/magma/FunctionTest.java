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
	
	@Test
	void compileFunctionWithParameters() {
		// Test function with a single parameter
		assertValid("fn accept(value : I32) => {}", "int32_t accept(int32_t value) {}");
		
		// Test function with multiple parameters
		assertValid("fn sum(a : I32, b : I32) => {return a + b;}", 
				"int32_t sum(int32_t a, int32_t b) {return a + b;}");
		
		// Test function with parameters of different types
		assertValid("fn process(count : U64, flag : Bool) : Void => {}", 
				"void process(uint64_t count, bool flag) {}");
	}
	
	@Test
	void compileFunctionCalls() {
		// Test simple function call
		assertValid("fn accept(value : I32) => {} accept(100);", 
				"int32_t accept(int32_t value) {} accept(100);");
		
		// Test function call with multiple arguments
		assertValid("fn sum(a : I32, b : I32) => {return a + b;} sum(5, 10);", 
				"int32_t sum(int32_t a, int32_t b) {return a + b;} sum(5, 10);");
		
		// Test function call with variables as arguments
		assertValid("let x = 10; let y = 20; fn add(a : I32, b : I32) => {return a + b;} add(x, y);", 
				"int32_t x = 10; int32_t y = 20; int32_t add(int32_t a, int32_t b) {return a + b;} add(x, y);");
	}
	
	@Test
	void compileMultipleFunctions() {
		// Test multiple function declarations
		assertValid("fn empty0() => {} fn empty1() => {}", 
				"int empty0() {} int empty1() {}");
		
		// Test multiple functions with different return types
		assertValid("fn func1() : I32 => {return 42;} fn func2() : U64 => {return 100;}", 
				"int32_t func1() {return 42;} uint64_t func2() {return 100;}");
		
		// Test multiple functions with parameters
		assertValid("fn add(a : I32, b : I32) => {return a + b;} fn multiply(x : I32, y : I32) => {return x * y;}", 
				"int32_t add(int32_t a, int32_t b) {return a + b;} int32_t multiply(int32_t x, int32_t y) {return x * y;}");
	}
}