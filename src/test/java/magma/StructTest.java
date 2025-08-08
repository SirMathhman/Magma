package magma;

import org.junit.jupiter.api.Test;

/**
 * Tests for struct declaration and usage in the Magma compiler.
 */
class StructTest extends BaseCompilerTest {
	@Test
	void compileEmptyStruct() {
		assertValid("struct Empty {}", "struct Empty {};");
	}

	@Test
	void compileStructWithMembers() {
		assertValid("struct Point {x : I32, y : I32}", "struct Point {int32_t x; int32_t y;};");
		assertValid("struct Data {name : I32, value : I32, flag : Bool}", 
			"struct Data {int32_t name; int32_t value; bool flag;};");
		// Ensure we don't need a comma after the last member
		assertInvalid("struct Point {x : I32, y : I32,}");
	}

 @Test
 void compileStructInitialization() {
     // Test the case from the issue description directly
     assertValid("struct Wrapper { value : I32 } let x : Wrapper = Wrapper { 100 };",
         "struct Wrapper {int32_t value;}; Wrapper x = {100};");
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
	void compileStructWithMethod() {
		// Test struct with impl block and method
		assertValid("""
										struct Example {} impl Example {   fn doSomething() => {     return 100;   } } \
										let value = Example {}; let result = value.doSomething();""",

								"""
										struct Example {} int Example_doSomething(Example* this) {   return 100; } \
										Example value = {}; int result = Example_doSomething(&value);""");
	}

	@Test
	void compileStructWithMultipleMethods() {
		// Test struct with multiple methods
		assertValid("""
										struct Counter { count : I32 } impl Counter {   fn getValue() => {     return this.count; \
										  }   fn increment() => {     return this.count + 1;   } } let c = Counter { 5 }; \
										let v1 = c.getValue(); let v2 = c.increment();""",

								"""
										struct Counter {int32_t count;} int32_t Counter_getValue(Counter* this) {   return this->count; \
										} int32_t Counter_increment(Counter* this) {   return this->count + 1; } \
										Counter c = {5}; int32_t v1 = Counter_getValue(&c); int32_t v2 = Counter_increment(&c);""");
	}
}