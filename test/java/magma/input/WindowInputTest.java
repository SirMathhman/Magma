package magma.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the WindowInput class to verify it works correctly and can only be created from other inputs.
 */
public class WindowInputTest {

	@Test
	void testWindowCreationFromRootInput() {
		// Create a root input
		RootInput rootInput = new RootInput("Hello, World!", "test-source");

		// Create a window from the root input
		Input windowInput = rootInput.window(0, 5);

		// Verify window content and properties
		assertEquals("Hello", windowInput.getContent()); assertEquals("test-source (window)", windowInput.getSource());
		assertEquals(0, windowInput.getStartIndex()); assertEquals(5, windowInput.getEndIndex());

		// Verify it's a WindowInput by checking if it has a parent
		assertTrue(windowInput instanceof WindowInput); assertEquals(rootInput, ((WindowInput) windowInput).getParent());
	}

	@Test
	void testWindowCreationFromWindowInput() {
		// Create a root input
		RootInput rootInput = new RootInput("Hello, World!", "test-source");

		// Create a window from the root input
		Input firstWindow = rootInput.window(0, 7);

		// Create another window from the first window
		Input secondWindow = firstWindow.window(0, 5);

		// Verify second window content and properties
		assertEquals("Hello", secondWindow.getContent());
		assertEquals("test-source (window) (window)", secondWindow.getSource());
		assertEquals(0, secondWindow.getStartIndex()); assertEquals(5, secondWindow.getEndIndex());

		// Verify it's a WindowInput by checking if it has a parent
		assertTrue(secondWindow instanceof WindowInput);
		assertEquals(firstWindow, ((WindowInput) secondWindow).getParent());
	}

	@Test
	void testWindowWithOffset() {
		RootInput rootInput = new RootInput("Hello, World!", "test-source");

		// Create a window with an offset
		Input windowInput = rootInput.window(7, 5);

		// Verify window content and properties
		assertEquals("World", windowInput.getContent()); assertEquals("test-source (window)", windowInput.getSource());
		assertEquals(7, windowInput.getStartIndex()); assertEquals(12, windowInput.getEndIndex());
	}

	@Test
	void testWindowInvalidParameters() {
		RootInput rootInput = new RootInput("Hello, World!", "test-source");

		// Test negative offset
		assertThrows(IllegalArgumentException.class, () -> rootInput.window(-1, 5));

		// Test negative length
		assertThrows(IllegalArgumentException.class, () -> rootInput.window(0, -1));

		// Test window extending beyond content
		assertThrows(IllegalArgumentException.class, () -> rootInput.window(0, 20));
		assertThrows(IllegalArgumentException.class, () -> rootInput.window(10, 5));
	}

	@Test
	void testWindowOperations() {
		RootInput rootInput = new RootInput("Hello, World!", "test-source"); Input windowInput = rootInput.window(0, 5);

		// Test afterPrefix on window
		Input afterPrefix = windowInput.afterPrefix("He"); assertEquals("llo", afterPrefix.getContent());
		assertEquals("test-source (window) (after prefix)", afterPrefix.getSource());

		// Test beforeSuffix on window
		Input beforeSuffix = windowInput.beforeSuffix("lo"); assertEquals("Hel", beforeSuffix.getContent());
		assertEquals("test-source (window) (before suffix)", beforeSuffix.getSource());

		// Test splitAtInfix on window
		Input[] parts = windowInput.splitAtInfix("l"); assertEquals(2, parts.length);
		assertEquals("He", parts[0].getContent()); assertEquals("lo", parts[1].getContent());
	}

	@Test
	void testExtendWindow() {
		RootInput rootInput = new RootInput("Hello, World!", "test-source"); Input windowInput = rootInput.window(0, 5);

		// Test extending with string
		Input extendedStart = windowInput.extendStart("Pre-"); assertEquals("Pre-Hello", extendedStart.getContent());
		assertEquals("test-source (window) (extended start)", extendedStart.getSource());

		Input extendedEnd = windowInput.extendEnd("-Ext"); assertEquals("Hello-Ext", extendedEnd.getContent());
		assertEquals("test-source (window) (extended end)", extendedEnd.getSource());

		// Test extending with another input
		RootInput prefixInput = new RootInput("Pre-", "prefix-source");
		Input extendedWithInput = windowInput.extendStart(prefixInput);
		assertEquals("Pre-Hello", extendedWithInput.getContent());
		assertEquals("test-source (window) (extended start with prefix-source)", extendedWithInput.getSource());
	}
}