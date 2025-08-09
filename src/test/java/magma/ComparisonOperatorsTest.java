package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static magma.CompileAssert.assertInvalid;
import static magma.CompileAssert.assertValid;

/**
 * Tests for comparison operators in the Magma compiler.
 * This class tests the implementation of equality (==), inequality (!=),
 * less than (<), greater than (>), less than or equal (<=), and greater than or equal (>=)
 * operators with type checking to ensure compatibility between operands.
 */
public class ComparisonOperatorsTest {

    /**
     * Tests equality (==) and inequality (!=) operations with type checking.
     * Verifies that:
     * - Two numbers of the same type can be compared for equality/inequality
     * - Numbers of different types cannot be compared (throws CompileException)
     * - The result of comparison is a Bool type
     *
     * @param type  the type annotation (I8, I16, I32, etc.)
     * @param cType the corresponding C type
     */
    @ParameterizedTest(name = "should support equality operations with same types: {0}")
    @CsvSource({"I8, int8_t", "I16, int16_t", "I32, int32_t", "I64, int64_t", 
                "U8, uint8_t", "U16, uint16_t", "U32, uint32_t", "U64, uint64_t"})
    @DisplayName("Should support equality operations with proper type checking")
    public void shouldHandleEqualityOperations(String type, String cType) {
        // Test equality with default I32 type
        if (type.equals("I32")) {
            // Basic equality
            assertValid("let x = 5; let y = 10; let z : Bool = x == y;", 
                        "int32_t x = 5; int32_t y = 10; bool z = x == y;");
            
            // Basic inequality
            assertValid("let x = 5; let y = 10; let z : Bool = x != y;", 
                        "int32_t x = 5; int32_t y = 10; bool z = x != y;");
            
            // Test type incompatibility in equality
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z : Bool = x == y;");
            
            // Test type incompatibility in inequality
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z : Bool = x != y;");
        }
        
        // Test equality with explicit type
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : Bool = x == y;",
                    cType + " x = 5; " + cType + " y = 10; bool z = x == y;");
                    
        // Test inequality with explicit type
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : Bool = x != y;",
                    cType + " x = 5; " + cType + " y = 10; bool z = x != y;");
    }
    
    /**
     * Tests relational operators (<, >, <=, >=) with type checking.
     * Verifies that:
     * - Two numbers of the same type can be compared with relational operators
     * - Numbers of different types cannot be compared (throws CompileException)
     * - The result of comparison is a Bool type
     *
     * @param type  the type annotation (I8, I16, I32, etc.)
     * @param cType the corresponding C type
     */
    @ParameterizedTest(name = "should support relational operations with same types: {0}")
    @CsvSource({"I8, int8_t", "I16, int16_t", "I32, int32_t", "I64, int64_t", 
                "U8, uint8_t", "U16, uint16_t", "U32, uint32_t", "U64, uint64_t"})
    @DisplayName("Should support relational operations with proper type checking")
    public void shouldHandleRelationalOperations(String type, String cType) {
        // Test relational operators with default I32 type
        if (type.equals("I32")) {
            // Less than
            assertValid("let x = 5; let y = 10; let z : Bool = x < y;", 
                        "int32_t x = 5; int32_t y = 10; bool z = x < y;");
            
            // Greater than
            assertValid("let x = 5; let y = 10; let z : Bool = x > y;", 
                        "int32_t x = 5; int32_t y = 10; bool z = x > y;");
                        
            // Less than or equal
            assertValid("let x = 5; let y = 10; let z : Bool = x <= y;", 
                        "int32_t x = 5; int32_t y = 10; bool z = x <= y;");
                        
            // Greater than or equal
            assertValid("let x = 5; let y = 10; let z : Bool = x >= y;", 
                        "int32_t x = 5; int32_t y = 10; bool z = x >= y;");
            
            // Test type incompatibility in relational operations
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z : Bool = x < y;");
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z : Bool = x > y;");
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z : Bool = x <= y;");
            assertInvalid("let x : I32 = 5; let y : I64 = 10; let z : Bool = x >= y;");
        }
        
        // Test relational operators with explicit type
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : Bool = x < y;",
                    cType + " x = 5; " + cType + " y = 10; bool z = x < y;");
                    
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : Bool = x > y;",
                    cType + " x = 5; " + cType + " y = 10; bool z = x > y;");
                    
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : Bool = x <= y;",
                    cType + " x = 5; " + cType + " y = 10; bool z = x <= y;");
                    
        assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : Bool = x >= y;",
                    cType + " x = 5; " + cType + " y = 10; bool z = x >= y;");
    }
    
    /**
     * Tests equality operations with boolean values.
     * Verifies that:
     * - Boolean values can be compared for equality/inequality
     * - Boolean values cannot be compared with non-boolean values
     */
    @Test
    @DisplayName("Should support equality operations with boolean values")
    public void shouldHandleEqualityWithBooleans() {
        // Test equality with boolean literals
        assertValid("let x : Bool = true == false;", "bool x = true == false;");
        
        // Test inequality with boolean literals
        assertValid("let x : Bool = true != false;", "bool x = true != false;");
        
        // Test equality with boolean variables
        assertValid("let a : Bool = true; let b : Bool = false; let c : Bool = a == b;",
                    "bool a = true; bool b = false; bool c = a == b;");
                    
        // Test inequality with boolean variables
        assertValid("let a : Bool = true; let b : Bool = false; let c : Bool = a != b;",
                    "bool a = true; bool b = false; bool c = a != b;");
                    
        // Test type incompatibility (boolean and numeric)
        assertInvalid("let a : Bool = true; let b : I32 = 5; let c : Bool = a == b;");
        assertInvalid("let a : Bool = true; let b : I32 = 5; let c : Bool = a != b;");
    }
    
    /**
     * Tests that relational operators (<, >, <=, >=) cannot be used with boolean values.
     * Verifies that an error is thrown when attempting to use relational operators with boolean values.
     */
    @Test
    @DisplayName("Should reject relational operations with boolean values")
    public void shouldRejectRelationalWithBooleans() {
        // Test relational operators with boolean literals
        assertInvalid("let x : Bool = true < false;");
        assertInvalid("let x : Bool = true > false;");
        assertInvalid("let x : Bool = true <= false;");
        assertInvalid("let x : Bool = true >= false;");
        
        // Test relational operators with boolean variables
        assertInvalid("let a : Bool = true; let b : Bool = false; let c : Bool = a < b;");
        assertInvalid("let a : Bool = true; let b : Bool = false; let c : Bool = a > b;");
        assertInvalid("let a : Bool = true; let b : Bool = false; let c : Bool = a <= b;");
        assertInvalid("let a : Bool = true; let b : Bool = false; let c : Bool = a >= b;");
    }
}