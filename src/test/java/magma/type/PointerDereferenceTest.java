package magma.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertValid;

/**
 * Tests for pointer dereferencing operations in the Magma compiler.
 * Verifies the conversion of pointer dereferencing (*pointer) from Magma syntax to C syntax.
 */
public class PointerDereferenceTest {

	/**
	 * Tests basic pointer dereferencing with explicit type annotation.
	 * Tests the scenario: let x = 20; let y = &x; let z : I32 = *y;
	 */
	@Test
	@DisplayName("Should compile pointer dereferencing with type annotation")
	public void shouldCompilePointerDereferencing() {
		// Basic pointer dereferencing scenario from the issue
		assertValid("let x = 20; let y = &x; let z : I32 = *y;", 
					"int32_t x = 20; int32_t* y = &x; int32_t z = *y;");
	}
}