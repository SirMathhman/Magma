package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}

	@Test
	void letDeclaration() {
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	@Test
	void letDeclarationWithDifferentVariable() {
		assertValid("let counter = 42;", "int32_t counter = 42;");
	}

	@Test
	void invalidLetDeclaration() {
		assertInvalid("let x = ;");
		assertInvalid("let = 100;");
		assertInvalid("let x 100;");
	}

	@Test
	void letDeclarationWithI32Type() {
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}

	@Test
	void letDeclarationWithU8Type() {
		assertValid("let x : U8 = 100;", "uint8_t x = 100;");
	}

	@Test
	void letDeclarationWithU16Type() {
		assertValid("let x : U16 = 100;", "uint16_t x = 100;");
	}

	@Test
	void letDeclarationWithU32Type() {
		assertValid("let x : U32 = 100;", "uint32_t x = 100;");
	}

	@Test
	void letDeclarationWithU64Type() {
		assertValid("let x : U64 = 100;", "uint64_t x = 100;");
	}

	@Test
	void letDeclarationWithI8Type() {
		assertValid("let x : I8 = 100;", "int8_t x = 100;");
	}

	@Test
	void letDeclarationWithI16Type() {
		assertValid("let x : I16 = 100;", "int16_t x = 100;");
	}

	@Test
	void letDeclarationWithI64Type() {
		assertValid("let x : I64 = 100;", "int64_t x = 100;");
	}

	@Test
	void letDeclarationWithF32Type() {
		assertValid("let x : F32 = 0;", "float x = 0;");
	}

	@Test
	void letDeclarationWithF32TypeAndDecimal() {
		assertValid("let x : F32 = 0.5;", "float x = 0.5;");
	}

	@Test
	void letDeclarationWithF64Type() {
		assertValid("let x : F64 = 0;", "double x = 0;");
	}

	@Test
	void letDeclarationWithF64TypeAndDecimal() {
		assertValid("let x : F64 = 0.5;", "double x = 0.5;");
	}

	@Test
	void letDeclarationWithU64Suffix() {
		assertValid("let x = 100U64;", "uint64_t x = 100;");
	}

	@Test
	void letDeclarationWithU32Suffix() {
		assertValid("let x = 100U32;", "uint32_t x = 100;");
	}

	@Test
	void letDeclarationWithU16Suffix() {
		assertValid("let x = 100U16;", "uint16_t x = 100;");
	}

	@Test
	void letDeclarationWithU8Suffix() {
		assertValid("let x = 100U8;", "uint8_t x = 100;");
	}

	@Test
	void letDeclarationWithI64Suffix() {
		assertValid("let x = 100I64;", "int64_t x = 100;");
	}

	@Test
	void letDeclarationWithI32Suffix() {
		assertValid("let x = 100I32;", "int32_t x = 100;");
	}

	@Test
	void letDeclarationWithI16Suffix() {
		assertValid("let x = 100I16;", "int16_t x = 100;");
	}

	@Test
	void letDeclarationWithI8Suffix() {
		assertValid("let x = 100I8;", "int8_t x = 100;");
	}

	@Test
	void letDeclarationWithF32Suffix() {
		assertValid("let x = 0F32;", "float x = 0;");
	}

	@Test
	void letDeclarationWithF32SuffixAndDecimal() {
		assertValid("let x = 0.5F32;", "float x = 0.5;");
	}

	@Test
	void letDeclarationWithF64Suffix() {
		assertValid("let x = 0F64;", "double x = 0;");
	}

	@Test
	void letDeclarationWithF64SuffixAndDecimal() {
		assertValid("let x = 0.5F64;", "double x = 0.5;");
	}

	@Test
	void typeMismatchBetweenAnnotationAndSuffix() {
		assertInvalid("let x : I32 = 0U64;");
		assertInvalid("let x : U8 = 100I16;");
		assertInvalid("let x : I64 = 42U32;");
		assertInvalid("let x : F32 = 0F64;");
		assertInvalid("let x : F64 = 0.5F32;");
		assertInvalid("let x : I32 = 0F32;");
		assertInvalid("let x : F32 = 0I32;");
	}

	@Test
	void letDeclarationWithBoolTypeAndTrueValue() {
		assertValid("let x : Bool = true;", "bool x = true;");
	}

	@Test
	void letDeclarationWithBoolTypeAndFalseValue() {
		assertValid("let x : Bool = false;", "bool x = false;");
	}

	@Test
	void letDeclarationWithTrueValue() {
		assertValid("let flag = true;", "bool flag = true;");
	}

	@Test
	void letDeclarationWithFalseValue() {
		assertValid("let enabled = false;", "bool enabled = false;");
	}

	@Test
	void invalidBoolDeclaration() {
		assertInvalid("let x : Bool = 100;");
		assertInvalid("let x : I32 = true;");
		assertInvalid("let x = TRUE;");
		assertInvalid("let x = FALSE;");
	}

	@Test
	void arrayDeclaration() {
		assertValid("let values : *[U8; 3] = [1, 2, 3];", "uint8_t values[3] = {1, 2, 3};");
	}

	@Test
	void arrayDeclarationWithDifferentType() {
		assertValid("let counts : *[I32; 4] = [10, 20, 30, 40];", "int32_t counts[4] = {10, 20, 30, 40};");
	}

	@Test
	void invalidArrayDeclaration() {
		// Size mismatch: declared size 3 but provided 2 elements
		assertInvalid("let values : *[U8; 3] = [1, 2];");
		// Size mismatch: declared size 2 but provided 3 elements
		assertInvalid("let values : *[U8; 2] = [1, 2, 3];");
	}

	@Test
	void stringArrayDeclaration() {
		assertValid("let string : *[U8; 5] = \"Hello\";", "uint8_t string[5] = {72, 101, 108, 108, 111};");
	}

	@Test
	void stringArrayDeclarationWithDifferentType() {
		assertValid("let codes : *[I32; 3] = \"ABC\";", "int32_t codes[3] = {65, 66, 67};");
	}

	@Test
	void invalidStringArrayDeclaration() {
		// Size mismatch: declared size 6 but provided 5 characters
		assertInvalid("let string : *[U8; 6] = \"Hello\";");
		// Size mismatch: declared size 4 but provided 5 characters
		assertInvalid("let string : *[U8; 4] = \"Hello\";");
	}

	@Test
	void pointerDeclaration() {
		assertValid("let y : *I32 = &x;", "int32_t* y = &x;");
	}

	@Test
	void pointerDeclarationWithDifferentType() {
		assertValid("let ptr : *U8 = &value;", "uint8_t* ptr = &value;");
	}

	@Test
	void pointerDeclarationWithBoolType() {
		assertValid("let flag_ptr : *Bool = &flag;", "bool* flag_ptr = &flag;");
	}

	@Test
	void pointerDereferencing() {
		assertValid("let z : I32 = *y;", "int32_t z = *y;");
	}

	@Test
	void pointerDereferencingWithDifferentType() {
		assertValid("let value : U8 = *ptr;", "uint8_t value = *ptr;");
	}

	@Test
	void pointerDereferencingWithBoolType() {
		assertValid("let flag_value : Bool = *flag_ptr;", "bool flag_value = *flag_ptr;");
	}

	@Test
	void issueResolutionTest() {
		// Test the specific code snippet from the issue description
		assertValid("let x = 5;", "int32_t x = 5;");
		assertValid("let y : *I32 = &x;", "int32_t* y = &x;");
		assertValid("let z : I32 = *y;", "int32_t z = *y;");
	}
	
	@Test
	void array2DDeclaration() {
		assertValid("let array : *[U8; 2, 2] = [[1, 2], [3, 4]];", "uint8_t array[2][2] = {{1, 2}, {3, 4}};");
	}
	
	@Test
	void array2DDeclarationWithDifferentType() {
		assertValid("let matrix : *[I32; 3, 2] = [[10, 20], [30, 40], [50, 60]];", "int32_t matrix[3][2] = {{10, 20}, {30, 40}, {50, 60}};");
	}
	
	@Test
	void invalidArray2DDeclaration() {
		// Row count mismatch: declared 3 rows but provided 2
		assertInvalid("let matrix : *[I32; 3, 2] = [[10, 20], [30, 40]];");
		// Column count mismatch: declared 2 columns but provided 3 in the first row
		assertInvalid("let matrix : *[I32; 2, 2] = [[10, 20, 30], [40, 50]];");
		// Column count mismatch: declared 2 columns but provided 1 in the second row
		assertInvalid("let matrix : *[I32; 2, 2] = [[10, 20], [30]];");
	}
}