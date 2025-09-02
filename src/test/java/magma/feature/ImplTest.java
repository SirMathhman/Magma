package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class ImplTest {
	@Test
	void impl() {
		assertAllValidWithPrelude("""
			struct Wrapper {
				value : I32
			}
			
			impl Wrapper {
			}
			
			let wrapper = Wrapper { readInt() };
			wrapper.value
			""", "10", "10");
	}

	@Test
	void implWithOneMethod() {
		assertAllValidWithPrelude("""
			struct Wrapper {
				value : I32
			}
			
			impl Wrapper {
				fn getValue() : I32 => this.value;
			}
			
			let wrapper = Wrapper { readInt() };
			wrapper.getValue()
			""", "10", "10");
	}
}
