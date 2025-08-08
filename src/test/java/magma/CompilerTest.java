package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void compileNumericTypes() {
		// Explicit I32 type declaration
		assertValid("let x : I32 = 0;", "int32_t x = 0;");

		// All integer types
		// Unsigned
		assertValid("let x : U8 = 0;", "uint8_t x = 0;");
		assertValid("let x : U16 = 0;", "uint16_t x = 0;");
		assertValid("let x : U32 = 0;", "uint32_t x = 0;");
		assertValid("let x : U64 = 0;", "uint64_t x = 0;");
		// Signed
		assertValid("let x : I8 = 0;", "int8_t x = 0;");
		assertValid("let x : I16 = 0;", "int16_t x = 0;");
		assertValid("let x : I64 = 0;", "int64_t x = 0;");

		// Type suffix literal
		assertValid("let x = 0U8;", "uint8_t x = 0;");

		// Untyped integer defaults to I32
		assertValid("let x = 200;", "int32_t x = 200;");
	}

	private void assertValid(String input, String output) {
		try {
			String actual = Compiler.compile(input);
			System.out.println("[DEBUG_LOG] Input: '" + input + "'");
			System.out.println("[DEBUG_LOG] Expected: '" + output + "'");
			System.out.println("[DEBUG_LOG] Actual: '" + actual + "'");
			assertEquals(output, actual);
		} catch (CompileException e) {
			System.out.println("[DEBUG_LOG] CompileException: " + e.getMessage());
			fail(e);
		}
	}

	@Test
	void compileBooleanTypes() {
		// Typed boolean literals
		assertValid("let x : Bool = true;", "bool x = true;");

		// Comparisons in typed Bool
		assertValid("let x : Bool = 1 < 2;", "bool x = 1 < 2;");

		// Untyped boolean defaults to Bool
		assertValid("let x = false;", "bool x = false;");
	}

	@Test
	void compileLetFromIdentifier() {
		assertValid("let a = 1; let b = 2; let c = a >= b;", "int32_t a = 1; int32_t b = 2; bool c = a >= b;");
		assertValid("let x = 5; let y = x;", "int32_t x = 5; int32_t y = x;");
		// Also supports mutable let and subsequent assignment
		assertValid("let mut x = 5; x = 100;", "int32_t x = 5; x = 100;");
	}

	@Test
	void invalid() {
		assertInvalid("?");
		assertInvalid("let x = 5; x = 100;");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}

	@Test
	void compileBlockWithBraces() {
		assertValid("{}", "{}");
		assertValid("{let x = 100;}", "{int32_t x = 100;}");
		assertValid("let x = 100; {let y = x;}", "int32_t x = 100;{int32_t y = x;}");
		assertInvalid("{let x = 100;} let y = x;");
	}

	@Test
	void compileIfStatement() {
		assertValid("if (true) {}", "if (true) {}");
		assertValid("if (false) {}", "if (false) {}");
		assertValid("if (true) {let x = 100;}", "if (true) {int32_t x = 100;}");
		assertValid("let x = true; if (x) {let y = 200;}", "bool x = true; if (x) {int32_t y = 200;}");
		assertValid("let x = 1; let y = 2; if (x < y) {let z = 3;}",
								"int32_t x = 1; int32_t y = 2; if (x < y) {int32_t z = 3;}");
	}

	@Test
	void compileIfElseStatement() {
		assertValid("if (true) {} else {}", "if (true) {} else {}");
		assertValid("if (false) {} else {}", "if (false) {} else {}");
		assertValid("if (true) {let x = 100;} else {let y = 200;}", "if (true) {int32_t x = 100;} else {int32_t y = 200;}");
		assertValid("let x = true; if (x) {let y = 200;} else {let z = 300;}",
								"bool x = true; if (x) {int32_t y = 200;} else {int32_t z = 300;}");
		assertValid("let x = 1; let y = 2; if (x < y) {let z = 3;} else {let z = 4;}",
								"int32_t x = 1; int32_t y = 2; if (x < y) {int32_t z = 3;} else {int32_t z = 4;}");

		// Test that else requires braces
		assertInvalid("if (true) {} else let x = 5;");
		assertInvalid("if (true) {let x = 1;} else let y = 2;");
	}

	@Test
	void compileWhileStatement() {
		assertValid("while (true) {}", "while (true) {}");
		assertValid("while (false) {}", "while (false) {}");
		assertValid("while (true) {let x = 100;}", "while (true) {int32_t x = 100;}");
		assertValid("let x = true; while (x) {let y = 200;}", "bool x = true; while (x) {int32_t y = 200;}");
		assertValid("let x = 1; let y = 2; while (x < y) {let z = 3;}",
								"int32_t x = 1; int32_t y = 2; while (x < y) {int32_t z = 3;}");
	}

	@Test
	void compileEmptyStruct() {
		assertValid("struct Empty {}", "struct Empty {}");
	}
	
	@Test
	void compileStructWithMembers() {
		assertValid("struct Point {x : I32, y : I32}", "struct Point {int32_t x; int32_t y;}");
		assertValid("struct Data {name : I32, value : I32, flag : Bool}", 
			"struct Data {int32_t name; int32_t value; bool flag;}");
		// Ensure we don't need a comma after the last member
		assertInvalid("struct Point {x : I32, y : I32,}");
	}
	
	@Test
	void compileStructInitialization() {
		// Test the case from the issue description directly
		assertValid("struct Wrapper { value : I32 } let x : Wrapper = Wrapper { 100 };",
			"struct Wrapper {int32_t value;} Wrapper x = {100};");
	}
	
	@Test
	void compileStructFieldAccess() {
		// Test accessing struct fields with dot notation
		assertValid("struct Wrapper { value : I32 } let x : Wrapper = Wrapper { 100 }; let y = x.value;",
			"struct Wrapper {int32_t value;} Wrapper x = {100}; int32_t y = x.value;");
			
		// Test accessing struct fields with multiple fields
		assertValid("struct Point { x : I32, y : I32 } let p : Point = Point { 10, 20 }; let x = p.x; let y = p.y;",
			"struct Point {int32_t x; int32_t y;} Point p = {10, 20}; int32_t x = p.x; int32_t y = p.y;");
	}
	
	@Test
	void structFieldAssignmentIsInvalid() {
		// Test the case from the issue description directly
		assertInvalid("struct Wrapper { value : I32 } let x : Wrapper = Wrapper { 100 }; x.value = 200;");
	}
	
	@Test
	void compileFunctionDeclaration() {
		// Test the empty function declaration from the issue description
		assertValid("fn empty() : Void => {}", "void empty() {}");
	}
}