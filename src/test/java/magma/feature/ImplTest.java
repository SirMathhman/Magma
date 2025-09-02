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
				fn add(other : I32) : I32 => this.value + other;
			}
			
			let wrapper = Wrapper { readInt() };
			wrapper.add(readInt())
			""", "10\r\n20", "30");
	}
}
