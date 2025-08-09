package magma.struct;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Tests for struct declaration implementation in the Magma compiler.
 * Verifies the conversion of struct declarations from Magma syntax to C syntax.
 */
public class StructDeclarationTest {

	/**
	 * Tests basic empty struct declaration.
	 * Tests that empty struct declarations are correctly compiled to C.
	 */
	@Test
	@DisplayName("Should compile empty struct declaration")
	public void shouldCompileEmptyStructDeclaration() {
		// Empty struct declaration
		assertValid("struct Empty {}", "struct Empty {};");
	}

	/**
	 * Tests struct declaration with a single field.
	 * Verifies that a struct with a field and type is correctly compiled to C.
	 */
	@Test
	@DisplayName("Should compile struct declaration with a single field")
	public void shouldCompileStructWithSingleField() {
		// Struct with a single field
		assertValid("struct Wrapper { value : I32 }", "struct Wrapper { int32_t value; };");
	}

	/**
	 * Tests struct declaration with multiple fields.
	 * Verifies that a struct with multiple fields and types is correctly compiled to C.
	 */
	@Test
	@DisplayName("Should compile struct declaration with multiple fields")
	public void shouldCompileStructWithMultipleFields() {
		// Struct with multiple fields separated by commas
		assertValid("struct Point { x : I32, y : I32 }", "struct Point { int32_t x; int32_t y; };");

		// Struct with multiple fields separated by semicolons
		assertValid("struct Person { name : I32; age : U8 }", "struct Person { int32_t name; uint8_t age; };");

		// Struct with fields of different types
		assertValid("struct Data { id : U64, value : F32, valid : Bool }",
								"struct Data { uint64_t id; float value; bool valid; };");
	}

	/**
	 * Tests that struct declarations require proper syntax.
	 * Verifies that syntax errors are caught when required elements are missing.
	 */
	@Test
	@DisplayName("Should require proper syntax for struct declarations")
	public void shouldRequireProperStructSyntax() {
		// Missing struct keyword
		assertInvalid("Empty {}");

		// Missing struct name
		assertInvalid("struct {}");

		// Missing opening curly brace
		assertInvalid("struct Empty }");

		// Missing closing curly brace
		assertInvalid("struct Empty {");
	}

	/**
	 * Tests that struct field declarations require proper types.
	 * Verifies that invalid types in struct fields are caught during validation.
	 */
	@Test
	@DisplayName("Should validate struct field types")
	public void shouldRequireProperTypeForStructFields() {
		// Invalid type in a single field
		assertInvalid("struct Person { name : String }");

		// Invalid type in multiple fields (comma separated)
		assertInvalid("struct Point { x : I32, y : Float }");

		// Invalid type in multiple fields (semicolon separated)
		assertInvalid("struct Data { id : U64; size : Number }");

		// Multiple fields with both valid and invalid types
		assertInvalid("struct Mixed { valid : Bool, invalid : Object, count : I32 }");
	}

	/**
	 * Tests that struct field declarations require proper syntax.
	 * Verifies that syntax errors in field declarations are caught during validation.
	 */
	@Test
	@DisplayName("Should validate struct field declaration syntax")
	public void shouldRequireProperFieldSyntax() {
		// Missing field type
		assertInvalid("struct Person { name }");

		// Missing field name
		assertInvalid("struct Point { : I32 }");

		// Missing colon between name and type
		assertInvalid("struct Data { id I32 }");

		// Multiple fields with syntax errors
		assertInvalid("struct Mixed { valid : Bool, invalid, count : I32 }");

		// Missing comma/semicolon between fields
		assertInvalid("struct Person { name : I32 age : U8 }");
	}
}