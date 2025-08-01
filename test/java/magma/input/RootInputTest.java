package magma.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RootInput class to verify the string slicing methods work correctly.
 */
public class RootInputTest {

	@Test
	void testStartsWithAndEndsWith() {
		RootInput input = new RootInput("Hello, World!");

		assertTrue(input.startsWith("Hello")); assertTrue(input.endsWith("World!")); assertFalse(input.startsWith("world"));
		assertFalse(input.endsWith("hello"));
	}

	@Test
	void testAfterPrefix() {
		RootInput input = new RootInput("PrefixContent", "test-source");

		Input result = input.afterPrefix("Prefix"); assertEquals("Content", result.getContent());
		assertEquals("test-source (after prefix)", result.getSource());

		// Test exception
		assertThrows(IllegalArgumentException.class, () -> input.afterPrefix("NonExistent"));
	}

	@Test
	void testBeforeSuffix() {
		RootInput input = new RootInput("ContentSuffix", "test-source");

		Input result = input.beforeSuffix("Suffix"); assertEquals("Content", result.getContent());
		assertEquals("test-source (before suffix)", result.getSource());

		// Test exception
		assertThrows(IllegalArgumentException.class, () -> input.beforeSuffix("NonExistent"));
	}

	@Test
	void testIndexOf() {
		RootInput input = new RootInput("Left-Infix-Right");

		assertEquals(5, input.indexOf("Infix")); assertEquals(-1, input.indexOf("NonExistent"));
	}

	@Test
	void testSplitAtInfix() {
		RootInput input = new RootInput("Left-Infix-Right", "test-source");

		Input[] parts = input.splitAtInfix("-Infix-"); assertEquals(2, parts.length);
		assertEquals("Left", parts[0].getContent()); assertEquals("Right", parts[1].getContent());
		assertEquals("test-source (left part)", parts[0].getSource());
		assertEquals("test-source (right part)", parts[1].getSource());

		// Test exception
		assertThrows(IllegalArgumentException.class, () -> input.splitAtInfix("NonExistent"));
	}

	@Test
	void testPositionTracking() {
		// Test default constructor sets positions correctly
		RootInput input = new RootInput("Hello, World!"); assertEquals(0, input.getStartIndex());
		assertEquals(13, input.getEndIndex());

		// Test explicit position constructor
		RootInput customInput = new RootInput("Content", "source", 5, 12); assertEquals(5, customInput.getStartIndex());
		assertEquals(12, customInput.getEndIndex());

		// Test position preservation in afterPrefix
		RootInput prefixInput = new RootInput("PrefixContent", "test-source", 10, 23);
		Input afterPrefix = prefixInput.afterPrefix("Prefix"); assertEquals(16, afterPrefix.getStartIndex());
		assertEquals(23, afterPrefix.getEndIndex());

		// Test position preservation in beforeSuffix
		RootInput suffixInput = new RootInput("ContentSuffix", "test-source", 5, 18);
		Input beforeSuffix = suffixInput.beforeSuffix("Suffix"); assertEquals(5, beforeSuffix.getStartIndex());
		assertEquals(12, beforeSuffix.getEndIndex());

		// Test position preservation in splitAtInfix
		RootInput infixInput = new RootInput("Left-Infix-Right", "test-source", 100, 116);
		Input[] parts = infixInput.splitAtInfix("-Infix-"); assertEquals(100, parts[0].getStartIndex());
		assertEquals(104, parts[0].getEndIndex()); assertEquals(111, parts[1].getStartIndex());
		assertEquals(116, parts[1].getEndIndex());
	}

	@Test
	void testPrettyPrint() {
		// Test pretty printing with default source
		RootInput input = new RootInput("Hello, World!");
		assertEquals("\"Hello, World!\" (source: string, range: 0-13)", input.prettyPrint());

		// Test pretty printing with custom source and range
		RootInput customInput = new RootInput("Content", "test-source", 5, 12);
		assertEquals("\"Content\" (source: test-source, range: 5-12)", customInput.prettyPrint());
	}
}