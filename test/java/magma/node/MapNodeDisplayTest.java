package magma.node;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to verify the display method of MapNode works as expected.
 */
public class MapNodeDisplayTest {
	@Test
	void testNodeWithTypeTagAndStringProperties() {
		MapNode node = new MapNode(); node = (MapNode) node.retype("MyNode");
		node = (MapNode) node.withString("name", "value"); node = (MapNode) node.withString("another", "property");
		assertEquals("MyNode { another: \"property\", name: \"value\" }", node.display());
	}

	@Test
	void testNodeWithoutTypeTag() {
		MapNode node = new MapNode(); node = (MapNode) node.withString("name", "value");
		assertEquals(" { name: \"value\" }", node.display());
	}

	@Test
	void testNodeWithTypeTagAndNodeList() {
		MapNode node = new MapNode(); node = (MapNode) node.retype("ComplexNode");
		node = (MapNode) node.withString("name", "value");

		MapNode child1 = new MapNode(); child1 = (MapNode) child1.retype("Child1");
		child1 = (MapNode) child1.withString("id", "1");

		MapNode child2 = new MapNode(); child2 = (MapNode) child2.retype("Child2");
		child2 = (MapNode) child2.withString("id", "2");
		
		final List<Node> children = Arrays.asList(child1, child2);

		node = (MapNode) node.withNodeList("children", children);
		assertEquals("ComplexNode { name: \"value\", children: [2 nodes] }", node.display());
	}

	@Test
	void testNodeWithOnlyNodeList() {
		MapNode child1 = new MapNode(); child1 = (MapNode) child1.retype("Child1");
		child1 = (MapNode) child1.withString("id", "1");

		MapNode child2 = new MapNode(); child2 = (MapNode) child2.retype("Child2");
		child2 = (MapNode) child2.withString("id", "2");
		
		final List<Node> children = Arrays.asList(child1, child2);

		MapNode node = new MapNode(); node = (MapNode) node.retype("ListNode");
		node = (MapNode) node.withNodeList("items", children);
		assertEquals("ListNode { items: [2 nodes] }", node.display());
	}
}