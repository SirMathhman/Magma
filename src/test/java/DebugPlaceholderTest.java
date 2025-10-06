import magma.compile.Lang;
import magma.compile.Node;
import magma.option.Option;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

public class DebugPlaceholderTest {
	@Test
	public void testPlaceholderParsing() {
		String code = """
				public class TestClass {
					public void method() {
						System.out.println("Hello");
						int x = 5;
					}
				}
				""";

		System.out.println("=== Testing JFunctionSegment parsing ==="); System.out.flush();
		Result<Node, ?> result = Lang.JRoot().lex(code);

		if (result instanceof Ok<Node, ?>(var node)) {
			System.out.println("Lexed successfully"); System.out.flush(); printNode(node, 0); System.out.flush();
		} else if (result instanceof Err<?, ?>(var error)) {
			System.out.println("Lex failed: " + error); System.out.flush();
		}
	}

	private void printNode(Node node, int depth) {
		String indent = "  ".repeat(depth); System.out.println(indent + "Node:");

		if (node.maybeType instanceof Option.Some<?>(var type)) {
			System.out.println(indent + "  @type: " + type);
		}

		// Print string fields
		for (String key : node.getStringKeys()) {
			var value = node.findString(key); if (value instanceof Option.Some<?>(var str)) {
				String escaped = str.toString().replace("\n", "\\n").replace("\t", "\\t");
				System.out.println(indent + "  " + key + " (string): " + escaped.substring(0, Math.min(50, escaped.length())));
			}
		}

		// Print node fields
		node.nodes.forEach((key, child) -> {
			System.out.println(indent + "  " + key + " (node):"); printNode(child, depth + 2);
		});

		// Print node list fields
		node.nodeLists.forEach((key, children) -> {
			System.out.println(indent + "  " + key + " (list): " + children.size() + " items");
			for (int i = 0; i < children.size() && i < 5; i++) {
				System.out.println(indent + "    [" + i + "]:"); printNode(children.get(i), depth + 3);
			}
		});
	}
}
