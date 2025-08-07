package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

class BooleanTypeTest {
	// Tests for boolean type annotations
	@Test
	void letToBoolWithTypeAnnotation() {
		assertValid("let x : Bool = true;", "bool x = true;");
	}
	
	@Test
	void letToBoolWithTypeAnnotationFalse() {
		assertValid("let x : Bool = false;", "bool x = false;");
	}
	
	// Tests for boolean literals without type annotation
	@Test
	void letToBoolWithTrueLiteral() {
		assertValid("let x = true;", "bool x = true;");
	}
	
	@Test
	void letToBoolWithFalseLiteral() {
		assertValid("let x = false;", "bool x = false;");
	}
	
	// Test for mixed case boolean literals
	@Test
	void letToBoolWithMixedCaseLiterals() {
		assertValid("let x = True;", "bool x = true;");
		assertValid("let y = FALSE;", "bool y = false;");
	}
}