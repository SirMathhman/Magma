package magma;// ...imports moved to TestUtils when needed

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class BasicTest {
	@Test
	void pass() {
		assertValidWithPrelude("readInt()", "100", 100);
	}
}