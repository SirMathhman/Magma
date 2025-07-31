package magma.node;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Simple test to verify the display method of MapNode works as expected.
 * This is a temporary test file to verify the changes to the display method.
 */
public class MapNodeDisplayTest {
	@Test
	void testNodeWithTypeTagAndStringProperties() {
		final MapNode node = new MapNode(); node.retype("MyNode"); node.withString("name", "value");
		node.withString("another", "property");
		assertEquals("MyNode { another: \"property\", name: \"value\" }", node.display());
	}

	@Test
	void testNodeWithoutTypeTag() {
		final MapNode node = new MapNode(); node.withString("name", "value");
		assertEquals("Node { name: \"value\" }", node.display());
	}

	@Test
	void testNodeWithTypeTagAndNodeList() {
		final MapNode node = new MapNode(); node.retype("ComplexNode"); node.withString("name", "value");

		final Node child1 = new MapNode().retype("Child1").withString("id", "1");
		final Node child2 = new MapNode().retype("Child2").withString("id", "2");
		final List<Node> children = Arrays.asList(child1, child2);

		node.withNodeList("children", children);
		assertEquals("ComplexNode { name: \"value\", children: [2 nodes] }", node.display());
	}

	@Test
	void testNodeWithOnlyNodeList() {
		final Node child1 = new MapNode().retype("Child1").withString("id", "1");
		final Node child2 = new MapNode().retype("Child2").withString("id", "2");
		final List<Node> children = Arrays.asList(child1, child2);

		final MapNode node = new MapNode(); node.retype("ListNode"); node.withNodeList("items", children);
		assertEquals("ListNode { items: [2 nodes] }", node.display());
	}
}