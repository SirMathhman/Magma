import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.list.List;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

public class DiagnoseClassTag {

	@Test
	public void diagnoseClassTagIssue() {
		String input = """
				package magma.compile;

				@Actual
				public class JavaSerializer {
					public void test() {
					}
				}
				""";

		System.out.println("=== Diagnosing Class Tag Issue ===");
		System.out.println("Input:");
		System.out.println(input);

		Result<Node, CompileError> lexResult = Lang.JRoot().lex(input);

	if (lexResult instanceof Err<Node, CompileError>(CompileError error)) {
		System.err.println("❌ Lexing failed: " + error);
		fail("Lexing should succeed");
	}

	assertInstanceOf(Ok.class, lexResult, "Lexing should succeed");		Ok<Node, CompileError> nodeCompileErrorOk = (Ok<Node, CompileError>) lexResult;
		Node lexedNode = nodeCompileErrorOk.value();
		System.out.println("\n✅ Lexing succeeded");
		System.out.println("\nLexed AST:");
		System.out.println(lexedNode.format(0));

		// Now deserialize and see what happens
		Result<Lang.JRoot, CompileError> deserResult = JavaSerializer.deserialize(Lang.JRoot.class, lexedNode);

		if (deserResult instanceof Err<Lang.JRoot, CompileError>(CompileError error)) {
			System.err.println("\n❌ Deserialization failed:");
			System.err.println(error.display());

			// Let's debug - extract the class node and see what its type is
			System.out.println("\n=== Debugging ===");
			if (lexedNode.findNodeList("children") instanceof Some<List<Node>>(List<Node> children)) {
				System.out.println("Found " + children.size() + " children");
				for (int i = 0; i < children.size(); i++) {
					Node child = children.getOrNull(i);
					if (child.maybeType instanceof Some<String>(String type)) {
						System.out.println("Child " + i + " type: '" + type + "'");
						System.out.println("  Type length: " + type.length());
						System.out.println("  Type bytes: " + Arrays.toString(type.getBytes()));
						System.out.println("  Equals 'class': " + type.equals("class"));
						System.out.println("  Trimmed equals 'class': " + type.trim().equals("class"));
					}
				}
			}
		} else System.out.println("\n✅ Deserialization succeeded!");
	}
}
