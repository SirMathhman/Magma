package magma.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Tests for pointer type declarations in the Magma compiler.
 * Verifies the conversion of pointer type declarations (*TYPE) from Magma syntax to C syntax.
 */
public class PointerDeclarationTest {

	/**
	 * Tests basic pointer variable declaration with type annotation.
	 * Tests that pointer type annotations are correctly compiled to C pointer type.
	 */
	@Test
	@DisplayName("Should compile pointer type annotation")
	public void shouldCompilePointerTypeAnnotation() {
		// Basic pointer to I32 declaration
		assertValid("let x = 20; let y : *I32 = &x;", "int32_t x = 20; int32_t* y = &x;");

		// Pointer to other integer types
		assertValid("let x = 10I8; let y : *I8 = &x;", "int8_t x = 10; int8_t* y = &x;");
		assertValid("let x = 1000I16; let y : *I16 = &x;", "int16_t x = 1000; int16_t* y = &x;");
		assertValid("let x = 9999999I64; let y : *I64 = &x;", "int64_t x = 9999999; int64_t* y = &x;");

		// Pointer to unsigned integer types
		assertValid("let x = 20U32; let y : *U32 = &x;", "uint32_t x = 20; uint32_t* y = &x;");

		// Pointer to floating point types
		assertValid("let x = 3.14F32; let y : *F32 = &x;", "float x = 3.14; float* y = &x;");
		assertValid("let x = 3.14159F64; let y : *F64 = &x;", "double x = 3.14159; double* y = &x;");
	}

	/**
	 * Tests mutable pointer variable declarations.
	 * Tests that mutable pointer variables can be declared and reassigned.
	 */
	@Test
	@DisplayName("Should support mutable pointer variables")
	public void shouldSupportMutablePointerVariables() {
		// Mutable pointer variable
		assertValid("let x = 20; let mut y : *I32 = &x; let z = 30; y = &z;",
								"int32_t x = 20; int32_t* y = &x; int32_t z = 30; y = &z;");
	}

	/**
	 * Tests implicit address-of and dereferencing operations.
	 * Tests that variables can be implicitly converted to pointers and pointers can be dereferenced.
	 */
	@Test
	@DisplayName("Should support implicit address-of and dereferencing")
	public void shouldSupportImplicitAddressOfAndDereferencing() {
		// Test the exact issue example: implicit address-of and dereferencing
		assertValid("let x = 20; let y : *I32 = x; let z : I32 = *y;", 
		           "int32_t x = 20; int32_t* y = &x; int32_t z = *y;");
		
		// Additional test cases for various types
		assertValid("let a = 10I8; let b : *I8 = a; let c : I8 = *b;", 
		           "int8_t a = 10; int8_t* b = &a; int8_t c = *b;");
		
		assertValid("let f = 3.14F32; let g : *F32 = f; let h : F32 = *g;", 
		           "float f = 3.14; float* g = &f; float h = *g;");
	}

	/**
	 * Tests type compatibility validation for pointer variables.
	 * Tests that type incompatibility is caught when assigning values of different types.
	 */
	@Test
	@DisplayName("Should enforce pointer type compatibility")
	public void shouldEnforceTypeCompatibility() {
		// Type incompatibility with different pointer types
		assertInvalid("let x = 20I32; let y = 10I8; let p : *I32 = &y;");

		// Cannot assign pointer of different type
		assertInvalid("let x = 20I32; let y = 3.14F32; let p : *I32 = &y;");
	}
}