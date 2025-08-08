package magma;

import org.junit.jupiter.api.Test;

/**
 * Robustness tests for struct declaration and usage in the Magma compiler.
 */
class StructRobustnessTest extends BaseCompilerTest {
	@Test
	void testInvalidStructSyntax() {
		// Missing opening brace
		assertInvalid("struct Point x : I32, y : I32}");
		
		// Missing closing brace
		assertInvalid("struct Point {x : I32, y : I32");
		
		// Empty struct name
		assertInvalid("struct {} ");
		
		// Invalid struct name (starts with number)
		assertInvalid("struct 1Point {x : I32}");
	}
	
	@Test
	void testInvalidStructMembers() {
		// Missing member type
		assertInvalid("struct Point {x, y : I32}");
		
		// Invalid member type
		assertInvalid("struct Point {x : InvalidType}");
		
		// Missing member name
		assertInvalid("struct Point {: I32}");
		
		// Invalid member name (starts with number)
		assertInvalid("struct Point {1x : I32}");
	}
	
	@Test
	void testStructDeclarationOrder() {
		// Test struct declaration order - defining Inner before Outer
		assertValid("struct Inner { value : I32 } struct Outer { inner : Inner }", 
			"struct Inner {int32_t value;}; struct Outer {Inner inner;};");
		
		// Test that forward references are allowed - compiler processes structs in order but allows reference before definition
		assertValid("struct Outer { inner : Inner } struct Inner { value : I32 }",
			"struct Outer {Inner inner;}; struct Inner {int32_t value;};");
	}
	
	@Test
	void testStructInitializationEdgeCases() {
		// Test initializing with wrong number of values (too few)
		assertInvalid("struct Point {x : I32, y : I32} let p = Point { 10 };");
		
		// Test initializing with wrong number of values (too many)
		assertInvalid("struct Point {x : I32, y : I32} let p = Point { 10, 20, 30 };");
	}
	
	@Test
	void testStructNameConflicts() {
		// Duplicate struct name (the compiler might override the first definition)
		assertValid("struct Point {x : I32} struct Point {y : I32}",
			"struct Point {int32_t x;}; struct Point {int32_t y;};");
		
		// Variable with same name as struct (assuming this might be allowed by the compiler)
		assertValid("let Point = 10; struct Point {x : I32}",
			"int32_t Point = 10; struct Point {int32_t x;};");
	}
	
	@Test
	void testStructMethodEdgeCases() {
		// Method with parameters
		assertValid("struct Calculator {} impl Calculator { fn add(a : I32, b : I32) => { return a + b; } } " +
			"let calc = Calculator {}; let result = calc.add(5, 10);",
			"struct Calculator {}; int32_t Calculator_add(Calculator* this, int32_t a, int32_t b) {   return a + b; } " +
			"Calculator calc = {}; int32_t result = Calculator_add(&calc, 5, 10);");
		
		// Method with no parameters
		assertValid("struct Calculator {} impl Calculator { fn zero() => { return 0; } } " +
			"let calc = Calculator {}; let result = calc.zero();",
			"struct Calculator {}; int32_t Calculator_zero(Calculator* this) {   return 0; } " +
			"Calculator calc = {}; int32_t result = Calculator_zero(&calc);");
		
		// Method with multiple expressions in the body
		assertValid("struct Calculator {} impl Calculator { fn complex() => { let a = 5; let b = 10; return a + b; } } " +
			"let calc = Calculator {}; let result = calc.complex();",
			"struct Calculator {}; int32_t Calculator_complex(Calculator* this) {   let a = 5; let b = 10; return a + b; } " +
			"Calculator calc = {}; int32_t result = Calculator_complex(&calc);");
	}
	
	@Test
	void testStructMethodReturnTypeInference() {
		// Test return type inference for boolean values
		assertValid("struct Example {} impl Example { fn getBool() => { return true; } }", 
			"struct Example {}; bool Example_getBool(Example* this) {   return true; }");
		
		// Test return type inference for numbers (defaults to int32_t)
		assertValid("struct Example {} impl Example { fn getNumber() => { return 42; } }", 
			"struct Example {}; int32_t Example_getNumber(Example* this) {   return 42; }");
	}
	
	@Test
	void testMethodCallValidation() {
		// Test valid method call
		assertValid("struct Example {} impl Example { fn test() => { return 0; } } " +
			"let e = Example {}; let result = e.test();",
			"struct Example {}; int32_t Example_test(Example* this) {   return 0; } " +
			"Example e = {}; int32_t result = Example_test(&e);");
		
		// Test method call with arguments
		assertValid("struct Example {} impl Example { fn test(a : I32) => { return a; } } " +
			"let e = Example {}; let result = e.test(42);",
			"struct Example {}; int32_t Example_test(Example* this, int32_t a) {   return a; } " +
			"Example e = {}; int32_t result = Example_test(&e, 42);");
	}
	
	@Test
	void testStructFieldAccess() {
		// Test standard field access
		assertValid("struct Point {x : I32, y : I32} let p = Point {10, 20}; let x = p.x; let y = p.y;",
			"struct Point {int32_t x; int32_t y;}; Point p = {10, 20}; int32_t x = p.x; int32_t y = p.y;");
		
		// Test field access in expressions
		assertValid("struct Point {x : I32, y : I32} let p = Point {10, 20}; let sum = p.x + p.y;",
			"struct Point {int32_t x; int32_t y;}; Point p = {10, 20}; int32_t sum = p.x + p.y;");
	}
	
	@Test
	void testPrimitiveOperationsWithStructFields() {
		// Test using struct fields in arithmetic operations
		assertValid("struct Point {x : I32, y : I32} let p = Point {10, 20}; let sum = p.x + p.y;",
			"struct Point {int32_t x; int32_t y;}; Point p = {10, 20}; int32_t sum = p.x + p.y;");
	}
}