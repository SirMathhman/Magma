package magma;

import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.option.Option;
import magma.result.Ok;
import magma.result.Result;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SerializeRoundtripTest {

	// Small leaf record used by several tests
	public record Leaf(String name) {
	}

	// 1) String holder
	public record RString(String value) {
	}

	// 2) Nested record holder
	public record RNode(Leaf child) {
	}

	// 3) List of nested records
	public record RList(List<Leaf> children) {
	}

	// 4) Optional string
	public record ROptString(Option<String> maybe) {
	}

	// 5) Optional node
	public record ROptNode(Option<Leaf> maybe) {
	}

	// 6) Optional list of nodes
	public record ROptList(Option<List<Leaf>> maybe) {
	}

	// Helper to assert Result is Ok and extract value
	private static <T> T assertOkExtract(Result<T, CompileError> result) {
		assertTrue(result instanceof Ok<?, ?>, () -> "Expected Ok but got: " + result);
		return ((Ok<T, CompileError>) result).value();
	}

	// --- Serialize -> Deserialize tests ---

	@Test
	public void serializeThenDeserialize_String() {
		final RString original = new RString("hello");
		final Result<Node, CompileError> s = Serialize.serialize(RString.class, original);
		Node node = assertOkExtract(s);

		final Result<RString, CompileError> d = Serialize.deserialize(RString.class, node);
		RString round = assertOkExtract(d);
		assertEquals(original, round);
	}

	@Test
	public void serializeThenDeserialize_Node() {
		final Leaf leaf = new Leaf("leafy");
		final RNode original = new RNode(leaf);

		final Result<Node, CompileError> s = Serialize.serialize(RNode.class, original);
		Node node = assertOkExtract(s);

		final Result<RNode, CompileError> d = Serialize.deserialize(RNode.class, node);
		RNode round = assertOkExtract(d);
		assertEquals(original, round);
	}

	@Test
	public void serializeThenDeserialize_ListNode() {
		final Leaf a = new Leaf("a");
		final Leaf b = new Leaf("b");
		final RList original = new RList(List.of(a, b));

		final Result<Node, CompileError> s = Serialize.serialize(RList.class, original);
		Node node = assertOkExtract(s);

		final Result<RList, CompileError> d = Serialize.deserialize(RList.class, node);
		RList round = assertOkExtract(d);
		assertEquals(original, round);
	}

	@Test
	public void serializeThenDeserialize_OptionString() {
		final ROptString original = new ROptString(Option.of("present"));

		final Result<Node, CompileError> s = Serialize.serialize(ROptString.class, original);
		Node node = assertOkExtract(s);

		final Result<ROptString, CompileError> d = Serialize.deserialize(ROptString.class, node);
		ROptString round = assertOkExtract(d);
		assertEquals(original, round);
	}

	@Test
	public void serializeThenDeserialize_OptionNode() {
		final Leaf leaf = new Leaf("optleaf");
		final ROptNode original = new ROptNode(Option.of(leaf));

		final Result<Node, CompileError> s = Serialize.serialize(ROptNode.class, original);
		Node node = assertOkExtract(s);

		final Result<ROptNode, CompileError> d = Serialize.deserialize(ROptNode.class, node);
		ROptNode round = assertOkExtract(d);
		assertEquals(original, round);
	}

	@Test
	public void serializeThenDeserialize_OptionList() {
		final Leaf a = new Leaf("x");
		final Leaf b = new Leaf("y");
		final ROptList original = new ROptList(Option.of(List.of(a, b)));

		final Result<Node, CompileError> s = Serialize.serialize(ROptList.class, original);
		Node node = assertOkExtract(s);

		final Result<ROptList, CompileError> d = Serialize.deserialize(ROptList.class, node);
		ROptList round = assertOkExtract(d);
		assertEquals(original, round);
	}

	// --- Deserialize -> Serialize tests ---

	@Test
	public void deserializeThenSerialize_String() {
		Node node = new Node().withString("value", "hi");

		final Result<RString, CompileError> d = Serialize.deserialize(RString.class, node);
		RString value = assertOkExtract(d);

		final Result<Node, CompileError> s = Serialize.serialize(RString.class, value);
		Node node2 = assertOkExtract(s);
		assertEquals("hi", node2.findString("value").map(v -> v).orElse(null));
	}

	@Test
	public void deserializeThenSerialize_Node() {
		Node child = new Node().withString("name", "inner");
		Node node = new Node().withNode("child", child);

		final Result<RNode, CompileError> d = Serialize.deserialize(RNode.class, node);
		RNode value = assertOkExtract(d);

		final Result<Node, CompileError> s = Serialize.serialize(RNode.class, value);
		Node node2 = assertOkExtract(s);
		Node childNode = node2.findNode("child").orElse(null);
		assertNotNull(childNode);
		assertEquals("inner", childNode.findString("name").map(v -> v).orElse(null));
	}

	@Test
	public void deserializeThenSerialize_ListNode() {
		Node c1 = new Node().withString("name", "n1");
		Node c2 = new Node().withString("name", "n2");
		Node node = new Node().withNodeList("children", List.of(c1, c2));

		final Result<RList, CompileError> d = Serialize.deserialize(RList.class, node);
		RList value = assertOkExtract(d);

		final Result<Node, CompileError> s = Serialize.serialize(RList.class, value);
		Node node2 = assertOkExtract(s);
		assertEquals(2, node2.findNodeList("children").orElse(List.of()).size());
	}

	@Test
	public void deserializeThenSerialize_OptionString() {
		Node node = new Node().withString("maybe", "optval");

		final Result<ROptString, CompileError> d = Serialize.deserialize(ROptString.class, node);
		ROptString value = assertOkExtract(d);

		final Result<Node, CompileError> s = Serialize.serialize(ROptString.class, value);
		Node node2 = assertOkExtract(s);
		assertEquals("optval", node2.findString("maybe").map(v -> v).orElse(null));
	}

	@Test
	public void deserializeThenSerialize_OptionNode() {
		Node child = new Node().withString("name", "optinner");
		Node node = new Node().withNode("maybe", child);

		final Result<ROptNode, CompileError> d = Serialize.deserialize(ROptNode.class, node);
		ROptNode value = assertOkExtract(d);

		final Result<Node, CompileError> s = Serialize.serialize(ROptNode.class, value);
		Node node2 = assertOkExtract(s);
		Node maybeNode = node2.findNode("maybe").orElse(null);
		assertNotNull(maybeNode);
		assertEquals("optinner", maybeNode.findString("name").map(v -> v).orElse(null));
	}

	@Test
	public void deserializeThenSerialize_OptionList() {
		Node c1 = new Node().withString("name", "p1");
		Node c2 = new Node().withString("name", "p2");
		Node node = new Node().withNodeList("maybe", List.of(c1, c2));

		final Result<ROptList, CompileError> d = Serialize.deserialize(ROptList.class, node);
		ROptList value = assertOkExtract(d);

		final Result<Node, CompileError> s = Serialize.serialize(ROptList.class, value);
		Node node2 = assertOkExtract(s);
		assertEquals(2, node2.findNodeList("maybe").orElse(List.of()).size());
	}
}
