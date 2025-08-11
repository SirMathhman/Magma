package magma;

import org.junit.jupiter.api.Test;

class TemplateTest extends CompilerTestBase {
	@Test
	void constructor() {
		assertValid("struct Empty {} let value : Empty = Empty {};", "struct Empty {}; struct Empty value = {};");
	}

	@Test
	void monomorphizedTemplateConstructor() {
		assertValid("struct Wrapper<T> { value : T } let value : Wrapper<I32> = Wrapper<I32> { 100 };",
							"struct Wrapper_int32_t { int32_t value; }; struct Wrapper_int32_t value = { 100 };");
	}

	@Test
	void monomorphizedCallStatement() {
		assertValid("fn pass<T>(value : T) => {return value;} let value = pass(100);",
							"int32_t pass_int32_t(int32_t value){return value;} int32_t value = pass(100);");
	}
}