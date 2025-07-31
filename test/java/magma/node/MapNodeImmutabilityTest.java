package magma.node;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify the immutability of the MapNode class.
 */
public class MapNodeImmutabilityTest {

	@Test
	void testRetypeReturnsNewInstance() {
		MapNode original = new MapNode(); Node modified = original.retype("TestType");

		// Verify that a new instance was returned
		assertNotSame(original, modified);

		// Verify that the original was not modified
		assertEquals(Optional.empty(), original.type());

		// Verify that the new instance has the correct type
		assertEquals(Optional.of("TestType"), modified.type());
	}

	@Test
	void testWithStringReturnsNewInstance() {
		MapNode original = new MapNode(); Node modified = original.withString("key", "value");

		// Verify that a new instance was returned
		assertNotSame(original, modified);

		// Verify that the original was not modified
		assertTrue(original.findString("key").isEmpty());

		// Verify that the new instance has the correct property
		assertEquals(Optional.of("value"), modified.findString("key"));
	}

	@Test
	void testWithNodeListReturnsNewInstance() {
		MapNode original = new MapNode(); List<Node> nodes = Arrays.asList(new MapNode(), new MapNode());
		Node modified = original.withNodeList("key", nodes);

		// Verify that a new instance was returned
		assertNotSame(original, modified);

		// Verify that the original was not modified
		assertTrue(original.findNodeList("key").isEmpty());

		// Verify that the new instance has the correct property
		assertFalse(modified.findNodeList("key").isEmpty()); assertEquals(2, modified.findNodeList("key").get().size());
	}

	@Test
	void testMergeReturnsNewInstance() {
		MapNode original1 = new MapNode(); original1 = (MapNode) original1.withString("key1", "value1");

		MapNode original2 = new MapNode(); original2 = (MapNode) original2.withString("key2", "value2");

		Node merged = original1.merge(original2);

		// Verify that a new instance was returned
		assertNotSame(original1, merged); assertNotSame(original2, merged);

		// Verify that the originals were not modified
		assertEquals(Optional.of("value1"), original1.findString("key1"));
		assertTrue(original1.findString("key2").isEmpty());

		assertEquals(Optional.of("value2"), original2.findString("key2"));
		assertTrue(original2.findString("key1").isEmpty());

		// Verify that the merged instance has properties from both originals
		assertEquals(Optional.of("value1"), merged.findString("key1"));
		assertEquals(Optional.of("value2"), merged.findString("key2"));
	}

	@Test
	void testMergeWithOverlappingProperties() {
		MapNode original1 = new MapNode(); original1 = (MapNode) original1.withString("key", "value1");

		MapNode original2 = new MapNode(); original2 = (MapNode) original2.withString("key", "value2");

		Node merged = original1.merge(original2);

		// Verify that the merged instance has the value from the second node
		assertEquals(Optional.of("value2"), merged.findString("key"));
	}

	@Test
	void testMergeWithOverlappingTypeTag() {
		MapNode original1 = new MapNode(); original1 = (MapNode) original1.retype("Type1");

		MapNode original2 = new MapNode(); original2 = (MapNode) original2.retype("Type2");

		Node merged = original1.merge(original2);

		// Verify that the merged instance has the type from the first node
		assertEquals(Optional.of("Type1"), merged.type());
	}

	@Test
	void testChainedOperations() {
		MapNode original = new MapNode();

		// Chain multiple operations
		Node modified = original.retype("TestType").withString("key1", "value1").withString("key2", "value2");

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
	void testNodeListImmutability() {
		// Create a list of nodes
		List<Node> originalList = Arrays.asList(new MapNode().retype("Child1"), new MapNode().retype("Child2"));

		// Add the list to a node
		MapNode node = new MapNode(); node = (MapNode) node.withNodeList("children", originalList);

		// Modify the original list
		((List<Node>) originalList).set(0, new MapNode().retype("ModifiedChild"));

		// Verify that the node's list was not affected by the modification
		List<Node> retrievedList = node.findNodeList("children").get();
		assertEquals("Child1", retrievedList.get(0).type().get());
	}
}