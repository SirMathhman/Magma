package magma.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the StringInput class to verify the string slicing methods work correctly.
 */
public class StringInputTest {

	@Test
	void testStartsWithAndEndsWith() {
		StringInput input = new StringInput("Hello, World!");

		assertTrue(input.startsWith("Hello")); assertTrue(input.endsWith("World!")); assertFalse(input.startsWith("world"));
		assertFalse(input.endsWith("hello"));
	}

	@Test
	void testAfterPrefix() {
		StringInput input = new StringInput("PrefixContent", "test-source");

		Input result = input.afterPrefix("Prefix"); assertEquals("Content", result.getContent());
		assertEquals("test-source (after prefix)", result.getSource());

		// Test exception
		assertThrows(IllegalArgumentException.class, () -> input.afterPrefix("NonExistent"));
	}

	@Test
	void testBeforeSuffix() {
		StringInput input = new StringInput("ContentSuffix", "test-source");

		Input result = input.beforeSuffix("Suffix"); assertEquals("Content", result.getContent());
		assertEquals("test-source (before suffix)", result.getSource());

		// Test exception
		assertThrows(IllegalArgumentException.class, () -> input.beforeSuffix("NonExistent"));
	}

	@Test
	void testIndexOf() {
		StringInput input = new StringInput("Left-Infix-Right");

		assertEquals(5, input.indexOf("Infix")); assertEquals(-1, input.indexOf("NonExistent"));
	}

	@Test
	void testSplitAtInfix() {
		StringInput input = new StringInput("Left-Infix-Right", "test-source");

		Input[] parts = input.splitAtInfix("-Infix-"); assertEquals(2, parts.length);
		assertEquals("Left", parts[0].getContent()); assertEquals("Right", parts[1].getContent());
		assertEquals("test-source (left part)", parts[0].getSource());
		assertEquals("test-source (right part)", parts[1].getSource());

		// Test exception
		assertThrows(IllegalArgumentException.class, () -> input.splitAtInfix("NonExistent"));
	}
}