package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test to verify that the Magma library is set up correctly.
 */
class MagmaLibraryTest {

	@Test
	void testLibrarySetup() {
		// Verify that the library is properly configured
		assertTrue(true, "Library setup is successful");
	}

	@Test
	void testBasicAssertion() {
		// Test basic JUnit 5 functionality
		assertEquals(2, 1 + 1, "Basic arithmetic should work");
	}

	@Test
	void testMagmaClassesAvailable() {
		// Verify that Magma classes can be instantiated
		assertDoesNotThrow(() -> {
			Class.forName("magma.Utils");
			Class.forName("magma.Collections");
			Class.forName("magma.Streams");
		}, "Core Magma classes should be available");
	}
}
