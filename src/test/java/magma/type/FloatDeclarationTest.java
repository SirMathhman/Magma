package magma.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Tests for float type declarations in the Magma compiler.
 * Verifies the conversion of F32 type declarations from Magma syntax to C syntax.
 */
public class FloatDeclarationTest {

	/**
	 * Tests basic F32 variable declaration with type annotation.
	 * Tests that F32 type annotations are correctly compiled to C float type.
	 */
	@Test
	@DisplayName("Should compile F32 type annotation")
	public void shouldCompileF32TypeAnnotation() {
		// Basic F32 variable declaration with type annotation
		assertValid("let value : F32 = 0.0;", "float value = 0.0;");

		// F32 variable with non-zero value
		assertValid("let pi : F32 = 3.14159;", "float pi = 3.14159;");

		// F32 variable with negative value
		assertValid("let temp : F32 = -273.15;", "float temp = -273.15;");
	}

	/**
	 * Tests basic F32 variable declaration with type suffix.
	 * Tests that F32 type suffix is correctly compiled to C float type.
	 */
	@Test
	@DisplayName("Should compile F32 type suffix")
	public void shouldCompileF32TypeSuffix() {
		// Basic F32 variable declaration with type suffix
		assertValid("let value = 0.0F32;", "float value = 0.0;");

		// F32 variable with non-zero value
		assertValid("let pi = 3.14159F32;", "float pi = 3.14159;");

		// F32 variable with negative value
		assertValid("let temp = -273.15F32;", "float temp = -273.15;");
	}

	/**
	 * Tests mutable F32 variable declarations.
	 * Tests that mutable F32 variables can be declared and reassigned.
	 */
	@Test
	@DisplayName("Should support mutable F32 variables")
	public void shouldSupportMutableF32Variables() {
		// Mutable F32 variable with type annotation
		assertValid("let mut value : F32 = 0.0; value = 1.5;", "float value = 0.0; value = 1.5;");

		// Mutable F32 variable with type suffix
		assertValid("let mut temp = 20.5F32; temp = 25.0;", "float temp = 20.5; temp = 25.0;");
	}

	/**
	 * Tests F32 variable references.
	 * Tests that F32 variables can be referenced in other F32 variable declarations.
	 */
	@Test
	@DisplayName("Should support F32 variable references")
	public void shouldSupportF32VariableReferences() {
		// Reference to F32 variable in another F32 variable declaration
		assertValid("let x : F32 = 3.14; let y : F32 = x;", "float x = 3.14; float y = x;");
	}

	/**
	 * Tests type compatibility validation for F32 variables.
	 * Tests that type incompatibility is caught when assigning values of different types.
	 */
	@Test
	@DisplayName("Should enforce F32 type compatibility")
	public void shouldEnforceTypeCompatibility() {
		// Type incompatibility with integer literal
		assertInvalid("let x : F32 = 42I32;");

		// Type incompatibility with variable reference
		assertInvalid("let x = 42I32; let y : F32 = x;");

		// Type incompatibility in reassignment
		assertInvalid("let mut x : F32 = 3.14; let y = 42I32; x = y;");
	}
}