package magma.node;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify the immutability of the MapNode class.
 */
class MapNodeImmutabilityTest {

	@Test
	final void testRetypeReturnsNewInstance() {
		final Node original = new MapNode(); final Node modified = original.retype("TestType");

		// Verify that a new instance was returned
		assertNotSame(original, modified);

		// Verify that the original was not modified
		assertEquals(Optional.empty(), original.type());

		// Verify that the new instance has the correct type
		assertEquals(Optional.of("TestType"), modified.type());
	}

	@Test
	final void testWithStringReturnsNewInstance() {
		final Node original = new MapNode(); final Node modified = original.withString("key", "value");

		// Verify that a new instance was returned
		assertNotSame(original, modified);

		// Verify that the original was not modified
		assertTrue(original.findString("key").isEmpty());

		// Verify that the new instance has the correct property
		assertEquals(Optional.of("value"), modified.findString("key"));
	}

	@Test
	final void testWithNodeListReturnsNewInstance() {
		final Node original = new MapNode(); final List<Node> nodes = Arrays.asList(new MapNode(), new MapNode());
		final Node modified = original.withNodeList("key", nodes);

		// Verify that a new instance was returned
		assertNotSame(original, modified);

		// Verify that the original was not modified
		assertTrue(original.findNodeList("key").isEmpty());

		// Verify that the new instance has the correct property
		assertFalse(modified.findNodeList("key").isEmpty()); assertEquals(2, modified.findNodeList("key").get().size());
	}

	@Test
	final void testMergeWithOverlappingProperties() {
		MapNode original1 = new MapNode(); original1 = (MapNode) original1.withString("key", "value1");

		MapNode original2 = new MapNode(); original2 = (MapNode) original2.withString("key", "value2");

		final Node merged = original1.merge(original2);

		// Verify that the merged instance has the value from the second node
		assertEquals(Optional.of("value2"), merged.findString("key"));
	}

	@Test
	final void testMergeWithOverlappingTypeTag() {
		MapNode original1 = new MapNode(); original1 = (MapNode) original1.retype("Type1");

		MapNode original2 = new MapNode(); original2 = (MapNode) original2.retype("Type2");

		final Node merged = original1.merge(original2);

		// Verify that the merged instance has the type from the first node
		assertEquals(Optional.of("Type1"), merged.type());
	}

	@Test
	final void testChainedOperations() {
		final Node original = new MapNode();

		// Chain multiple operations
		final Node modified = original.retype("TestType").withString("key1", "value1").withString("key2", "value2");

		// Verify that a new instance was returned
		assertNotSame(original, modified);

		// Verify that the original was not modified
		assertEquals(Optional.empty(), original.type()); assertTrue(original.findString("key1").isEmpty());
		assertTrue(original.findString("key2").isEmpty());

		// Verify that the new instance has all the correct properties
		assertEquals(Optional.of("TestType"), modified.type());
		assertEquals(Optional.of("value1"), modified.findString("key1"));
		assertEquals(Optional.of("value2"), modified.findString("key2"));
	}

	@Test
	final void testNodeListImmutability() {
		// Create a list of nodes
		final List<Node> originalList = Arrays.asList(new MapNode().retype("Child1"), new MapNode().retype("Child2"));

		// Add the list to a node
		MapNode node = new MapNode(); node = (MapNode) node.withNodeList("children", originalList);

		// Modify the original list
		originalList.set(0, new MapNode().retype("ModifiedChild"));

		// Verify that the node's list was not affected by the modification
		final List<Node> retrievedList = node.findNodeList("children").get();
		assertEquals("Child1", retrievedList.getFirst().type().get());
	}
}