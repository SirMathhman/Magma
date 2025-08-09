package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static magma.CompileAssert.assertInvalid;
import static magma.CompileAssert.assertValid;

/**
 * Tests for arithmetic operations in the Magma compiler.
 * This class specifically tests addition, subtraction, and multiplication operations
 * with type checking to ensure compatibility between operands.
 */
public class ArithmeticOperationsTest {

    /**
     * Tests addition operations with type checking.
     * Verifies that:
     * - Two numbers of the same type can be added (for all supported types)
     * - Multiple numbers of the same type can be added in sequence (chained addition)
     * - Numbers of different types cannot be added (throws CompileException)
     *
     * @param type  the type annotation (I8, I16, I32, etc.)
     * @param cType the corresponding C type
     */
    @ParameterizedTest(name = "should support addition of same types: {0}")
    @CsvSource({"I8, int8_t", "I16, int16_t", "I32, int32_t", "I64, int64_t", "U8, uint8_t", "U16, uint16_t",
            "U32, uint32_t", "U64, uint64_t"})
    @DisplayName("Should support addition operations with proper type checking")
    public void shouldHandleAdditionOperations(String type, String cType) {
        // Test addition with default I32 type
        if (type.equals("I32")) {
            // Basic addition
            assertValid("let x = 5; let y = 10; let z = x + y;", "int32_t x = 5; int32_t y = 10; int32_t z = x + y;");

            // Chained addition with literals
            assertValid("let x = 3 + 5 + 7;", "int32_t x = 3 + 5 + 7;");

            // Chained addition with variables
            assertValid("let a = 3; let b = 5; let c = 7; let d = a + b + c;",
                    "int32_t a = 3; int32_t b = 5; int32_t c = 7; int32_t d = a + b + c;");

            // Chained addition with mixed literals and variables
            assertValid("let a = 3; let b = 5; let c = a + b + 7;", "int32_t a = 3; int32_t b = 5; int32_t c = a + b + 7;");

            // Test type incompatibility in addition
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z = x + y;");

            // Test type incompatibility in chained addition
            assertInvalid("let a : I32 = 3; let b : I64 = 5; let c = a + b + 7;");
        }

        // Test addition with explicit type
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : " + type + " = x + y;",
                cType + " x = 5; " + cType + " y = 10; " + cType + " z = x + y;");

        // Test chained addition with explicit type (only for I16 to demonstrate with a different type)
        if (type.equals("I16")) {
            assertValid("let a : I16 = 3; let b : I16 = 5; let c : I16 = a + b + 7;",
                    "int16_t a = 3; int16_t b = 5; int16_t c = a + b + 7;");
        }
    }

    /**
     * Tests subtraction operations with type checking.
     * Verifies that:
     * - Two numbers of the same type can be subtracted (for all supported types)
     * - Multiple numbers of the same type can be subtracted in sequence (chained subtraction)
     * - Numbers of different types cannot be subtracted (throws CompileException)
     *
     * @param type  the type annotation (I8, I16, I32, etc.)
     * @param cType the corresponding C type
     */
    @ParameterizedTest(name = "should support subtraction of same types: {0}")
    @CsvSource({"I8, int8_t", "I16, int16_t", "I32, int32_t", "I64, int64_t", "U8, uint8_t", "U16, uint16_t",
            "U32, uint32_t", "U64, uint64_t"})
    @DisplayName("Should support subtraction operations with proper type checking")
    public void shouldHandleSubtractionOperations(String type, String cType) {
        // Test subtraction with default I32 type
        if (type.equals("I32")) {
            // Basic subtraction
            assertValid("let x = 10; let y = 5; let z = x - y;", "int32_t x = 10; int32_t y = 5; int32_t z = x - y;");

            // Chained subtraction with literals
            assertValid("let x = 15 - 5 - 2;", "int32_t x = 15 - 5 - 2;");

            // Chained subtraction with variables
            assertValid("let a = 20; let b = 5; let c = 2; let d = a - b - c;",
                    "int32_t a = 20; int32_t b = 5; int32_t c = 2; int32_t d = a - b - c;");

            // Chained subtraction with mixed literals and variables
            assertValid("let a = 20; let b = 5; let c = a - b - 2;", "int32_t a = 20; int32_t b = 5; int32_t c = a - b - 2;");

            // Test type incompatibility in subtraction
            assertInvalid("let x : I32 = 10; let y : I64 = 5; let z = x - y;");

            // Test type incompatibility in chained subtraction
            assertInvalid("let a : I32 = 15; let b : I64 = 5; let c = a - b - 2;");
        }

        // Test subtraction with explicit type
        assertValid("let x : " + type + " = 10; let y : " + type + " = 5; let z : " + type + " = x - y;",
                cType + " x = 10; " + cType + " y = 5; " + cType + " z = x - y;");

        // Test chained subtraction with explicit type (only for I16 to demonstrate with a different type)
        if (type.equals("I16")) {
            assertValid("let a : I16 = 15; let b : I16 = 5; let c : I16 = a - b - 2;",
                    "int16_t a = 15; int16_t b = 5; int16_t c = a - b - 2;");
        }
    }

    /**
     * Tests multiplication operations with type checking.
     * Verifies that:
     * - Two numbers of the same type can be multiplied (for all supported types)
     * - Multiple numbers of the same type can be multiplied in sequence (chained multiplication)
     * - Numbers of different types cannot be multiplied (throws CompileException)
     *
     * @param type  the type annotation (I8, I16, I32, etc.)
     * @param cType the corresponding C type
     */
    @ParameterizedTest(name = "should support multiplication of same types: {0}")
    @CsvSource({"I8, int8_t", "I16, int16_t", "I32, int32_t", "I64, int64_t", "U8, uint8_t", "U16, uint16_t",
            "U32, uint32_t", "U64, uint64_t"})
    @DisplayName("Should support multiplication operations with proper type checking")
    public void shouldHandleMultiplicationOperations(String type, String cType) {
        // Test multiplication with default I32 type
        if (type.equals("I32")) {
            // Basic multiplication
            assertValid("let x = 5; let y = 10; let z = x * y;", "int32_t x = 5; int32_t y = 10; int32_t z = x * y;");

            // Chained multiplication with literals
            assertValid("let x = 3 * 5 * 7;", "int32_t x = 3 * 5 * 7;");

            // Chained multiplication with variables
            assertValid("let a = 3; let b = 5; let c = 7; let d = a * b * c;",
                    "int32_t a = 3; int32_t b = 5; int32_t c = 7; int32_t d = a * b * c;");

            // Chained multiplication with mixed literals and variables
            assertValid("let a = 3; let b = 5; let c = a * b * 7;", "int32_t a = 3; int32_t b = 5; int32_t c = a * b * 7;");

            // Test type incompatibility in multiplication
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z = x * y;");

            // Test type incompatibility in chained multiplication
            assertInvalid("let a : I32 = 3; let b : I64 = 5; let c = a * b * 7;");
        }

        // Test multiplication with explicit type
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : " + type + " = x * y;",
                cType + " x = 5; " + cType + " y = 10; " + cType + " z = x * y;");

        // Test chained multiplication with explicit type (only for I16 to demonstrate with a different type)
        if (type.equals("I16")) {
            assertValid("let a : I16 = 3; let b : I16 = 5; let c : I16 = a * b * 7;",
                    "int16_t a = 3; int16_t b = 5; int16_t c = a * b * 7;");
        }
    }
    
    /**
     * Tests support for parentheses in arithmetic expressions.
     * Verifies that:
     * - Parentheses are respected in arithmetic expressions
     * - Operations inside parentheses are evaluated first
     * - Nested parentheses are supported
     * - Type checking works correctly with parentheses
     */
    @DisplayName("Should support parentheses in arithmetic expressions")
    public void shouldSupportParenthesesInArithmeticExpressions() {
        // Test basic parentheses usage with addition and multiplication
        assertValid("let x = (3 + 4) * 7;", "int32_t x = (3 + 4) * 7;");
        
        // Test nested parentheses
        assertValid("let x = (2 + (3 * 4)) * 5;", "int32_t x = (2 + (3 * 4)) * 5;");
        
        // Test parentheses with variables
        assertValid("let a = 3; let b = 4; let c = (a + b) * 7;", 
                    "int32_t a = 3; int32_t b = 4; int32_t c = (a + b) * 7;");
        
        // Test type checking with parentheses
        assertValid("let a : I16 = 3; let b : I16 = 4; let c : I16 = (a + b) * 7;",
                    "int16_t a = 3; int16_t b = 4; int16_t c = (a + b) * 7;");
        
        // Test type incompatibility within parentheses
        assertInvalid("let a : I32 = 3; let b : I64 = 4; let c = (a + b) * 7;");
        
        // Test mixed operations with parentheses
        assertValid("let x = 3 + (4 * 5);", "int32_t x = 3 + (4 * 5);");
        assertValid("let x = (3 + 4) * (5 + 6);", "int32_t x = (3 + 4) * (5 + 6);");
        
        // Test complex expressions with parentheses
        assertValid("let x = ((2 + 3) * (4 - 1)) + (7 * 8);", "int32_t x = ((2 + 3) * (4 - 1)) + (7 * 8);");
        
        // Test with different types but consistent within parentheses
        assertValid("let a : I16 = 3; let b : I16 = 4; let c : I32 = 5; let d = (a + b) * c;",
                    "int16_t a = 3; int16_t b = 4; int32_t c = 5; int32_t d = (a + b) * c;");
    }
}