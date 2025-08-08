package magma;

import org.junit.jupiter.api.Test;

/**
 * Tests to verify that generic struct declarations don't produce C code.
 */
class GenericStructDeclarationTest extends BaseCompilerTest {
    
    @Test
    void genericStructDeclarationShouldNotProduceCode() {
        // Test that a standalone generic struct declaration doesn't produce any C code
        assertValid("struct MyWrapper<T> { value : T }", "");
        
        // Test with multiple type parameters
        assertValid("struct Pair<K, V> { key : K, value : V }", "");
        
        // Test with nested type parameters
        assertValid("struct Nested<T> { inner : T, value : I32 }", "");
    }
    
    @Test
    void genericStructWithConcreteTypesShouldProduceCode() {
        // Test that using a generic struct with concrete types produces the expected C code
        assertValid("struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 };",
                "struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100};");
    }
    
    @Test
    void multipleGenericStructsShouldNotInterfere() {
        // Test that multiple generic struct declarations don't interfere with each other
        assertValid(
                "struct Box<T> { value : T } struct Pair<A, B> { first : A, second : B } " +
                "let box : Box<I32> = Box<I32> { 42 }; let pair : Pair<Bool, I32> = Pair<Bool, I32> { true, 100 };",
                "struct Box_I32 {int32_t value;}; struct Pair_Bool_I32 {bool first; int32_t second;}; " +
                "Box_I32 box = {42}; Pair_Bool_I32 pair = {true, 100};");
    }
}