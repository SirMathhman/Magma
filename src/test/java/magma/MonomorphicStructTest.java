package magma;

import org.junit.jupiter.api.Test;

/**
 * Tests for monomorphic struct declaration and usage in the Magma compiler.
 */
class MonomorphicStructTest extends BaseCompilerTest {
	@Test
	void compileGenericStructWithPrimitiveType() {
		// Test the case from the issue description directly
		assertValid("struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 };",
								"struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100};");

		// Test with different primitive types
		assertValid("struct MyWrapper<T> { value : T } let b : MyWrapper<Bool> = MyWrapper<Bool> { true };",
								"struct MyWrapper_Bool {bool value;}; MyWrapper_Bool b = {true};");
	}

	@Test
	void compileGenericStructWithMultipleTypeParams() {
		// Test with multiple type parameters
		assertValid("struct Pair<K, V> { key : K, value : V } let pair : Pair<I32, Bool> = Pair<I32, Bool> { 42, true };",
								"struct Pair_I32_Bool {int32_t key; bool value;}; Pair_I32_Bool pair = {42, true};");
	}

	@Test
	void compileGenericStructFieldAccess() {
		// Test accessing fields of a generic struct instance
		assertValid(
				"struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 }; let v = wrapper.value;",
				"struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100}; int32_t v = wrapper.value;");

		// Test with multiple fields
		assertValid("struct Pair<K, V> { key : K, value : V } let pair : Pair<I32, Bool> = Pair<I32, Bool> { 42, true }; " +
								"let k = pair.key; let v = pair.value;",
								"struct Pair_I32_Bool {int32_t key; bool value;}; Pair_I32_Bool pair = {42, true}; " +
								"int32_t k = pair.key; bool v = pair.value;");
	}

	@Test
	void invalidGenericStructUsage() {
		// Missing type parameter in declaration
		assertInvalid("struct MyWrapper { value : T }");

		// Using undeclared type parameter
		assertInvalid("struct MyWrapper<T> { value : U }");

		// Missing type argument in usage
		assertInvalid("struct MyWrapper<T> { value : T } let wrapper : MyWrapper = MyWrapper { 100 };");

		// Wrong number of type arguments
		assertInvalid("struct Pair<K, V> { key : K, value : V } let pair : Pair<I32> = Pair<I32> { 42 };");

		// Type mismatch in initialization
		assertInvalid("struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { true };");

		// Type parameters with invalid names
		assertInvalid("struct MyWrapper<1T> { value : 1T }");
	}

	@Test
	void nestedGenericStructs() {
		// Test nested generic structs
		assertValid("struct Inner<T> { value : T } struct Outer<U> { inner : Inner<U> } " +
								"let inner : Inner<I32> = Inner<I32> { 42 }; " + "let outer : Outer<I32> = Outer<I32> { inner };",
								"struct Inner_I32 {int32_t value;}; struct Outer_I32 {Inner_I32 inner;}; " +
								"Inner_I32 inner = {42}; " + "Outer_I32 outer = {inner};");
	}
}