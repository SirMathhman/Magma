package magma;

import org.junit.jupiter.api.Test;

/**
 * Tests for the "class fn" syntax sugar feature in the Magma compiler.
 * 
 * The "class fn" syntax:
 * 
 * class fn Point(x : I32, y : I32) => {}
 * 
 * is sugar for:
 * 
 * struct Point {
 *   x : I32,
 *   y : I32
 * }
 * 
 * fn Point(x : I32, y : I32): Point => {
 *   let this : Point = Point { x, y };
 *   return this;
 * }
 */
class ClassFunctionTest extends BaseCompilerTest {
    
    @Test
    void testBasicClassFunction() {
        // Test the basic "class fn" syntax
        assertValid(
            "class fn Point(x : I32, y : I32) => {}",
            "struct Point {int32_t x; int32_t y;}; Point Point(int32_t x, int32_t y) {   Point this = {x, y}; return this; }"
        );
    }
    
    @Test
    void testClassFunctionWithMultipleParameters() {
        // Test with multiple parameters
        assertValid(
            "class fn Vector3(x : I32, y : I32, z : I32) => {}",
            "struct Vector3 {int32_t x; int32_t y; int32_t z;}; Vector3 Vector3(int32_t x, int32_t y, int32_t z) {   Vector3 this = {x, y, z}; return this; }"
        );
    }
    
    @Test
    void testClassFunctionWithNoParameters() {
        // Test with no parameters
        assertValid(
            "class fn Empty() => {}",
            "struct Empty {}; Empty Empty() {   Empty this = {}; return this; }"
        );
    }
    
    @Test
    void testClassFunctionWithDifferentTypes() {
        // Test with different parameter types
        assertValid(
            "class fn Mixed(name : String, age : I32, active : Bool) => {}",
            "struct Mixed {String name; int32_t age; bool active;}; Mixed Mixed(String name, int32_t age, bool active) {   Mixed this = {name, age, active}; return this; }"
        );
    }
    
    @Test
    void testClassFunctionUsage() {
        // Test using the constructor function
        assertValid(
            "class fn Point(x : I32, y : I32) => {} let p = Point(10, 20);",
            "struct Point {int32_t x; int32_t y;}; Point Point(int32_t x, int32_t y) {   Point this = {x, y}; return this; } Point p = Point(10, 20);"
        );
    }
    
    @Test
    void testClassFunctionWithFieldAccess() {
        // Test accessing fields from the created struct
        assertValid(
            "class fn Point(x : I32, y : I32) => {} let p = Point(10, 20); let sum = p.x + p.y;",
            "struct Point {int32_t x; int32_t y;}; Point Point(int32_t x, int32_t y) {   Point this = {x, y}; return this; } Point p = Point(10, 20); int32_t sum = p.x + p.y;"
        );
    }
    
    @Test
    void testInvalidClassFunctionSyntax() {
        // Missing opening parenthesis
        assertInvalid("class fn Pointx : I32, y : I32) => {}");
        
        // Missing closing parenthesis
        assertInvalid("class fn Point(x : I32, y : I32 => {}");
        
        // Missing opening brace
        assertInvalid("class fn Point(x : I32, y : I32) => }");
        
        // Missing closing brace
        assertInvalid("class fn Point(x : I32, y : I32) => {");
        
        // Missing arrow
        assertInvalid("class fn Point(x : I32, y : I32) {}");
        
        // Invalid class name (starts with number)
        assertInvalid("class fn 1Point(x : I32, y : I32) => {}");
    }
    
    @Test
    void testInvalidClassFunctionParameters() {
        // Missing parameter type
        assertInvalid("class fn Point(x, y : I32) => {}");
        
        // Invalid parameter type
        assertInvalid("class fn Point(x : InvalidType) => {}");
        
        // Missing parameter name
        assertInvalid("class fn Point(: I32) => {}");
        
        // Invalid parameter name (starts with number)
        assertInvalid("class fn Point(1x : I32) => {}");
    }
    
    @Test
    void testClassFunctionWithCustomBody() {
        // Test with a custom body that adds validation logic
        assertValid(
            "class fn PositivePoint(x : I32, y : I32) => { if (x < 0 || y < 0) { return PositivePoint(0, 0); } }",
            "struct PositivePoint {int32_t x; int32_t y;}; PositivePoint PositivePoint(int32_t x, int32_t y) {   if (x < 0 || y < 0) { return PositivePoint(0, 0); } PositivePoint this = {x, y}; return this; }"
        );
    }
}