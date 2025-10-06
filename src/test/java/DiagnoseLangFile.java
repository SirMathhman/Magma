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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

public class DiagnoseLangFile {

	@Test
	public void diagnoseLangFile() {
		try {
			String input = Files.readString(Paths.get("src/main/java/magma/compile/JavaSerializer.java"));

			System.out.println("=== Diagnosing JavaSerializer.java File ===");
			System.out.println("File length: " + input.length());

			Result<Node, CompileError> lexResult = Lang.JRoot().lex(input);

			if (lexResult instanceof Err<Node, CompileError>(CompileError error)) {
				System.err.println("❌ Lexing failed: " + error.display());
				fail("Lexing should succeed");
			}

			assertInstanceOf(Ok<?, ?>.class, lexResult, "Lexing should succeed");

			Ok<Node, CompileError> nodeCompileErrorOk = (Ok<Node, CompileError>) lexResult;
			Node lexedNode = nodeCompileErrorOk.value();
			System.out.println("\n✅ Lexing succeeded");

			// Find the class nodes
			System.out.println("\n=== Debugging Class Nodes ===");
			if (lexedNode.findNodeList("children") instanceof Some<List<Node>>(List<Node> children)) {
				System.out.println("Found " + children.size() + " children");
				for (int i = 0; i < children.size(); i++) {
					Node child = children.getOrNull(i);
					if (child.maybeType instanceof Some<String>(String type)) {
						System.out.println("\nChild " + i + ":");
						System.out.println("  Type: '" + type + "'");
						System.out.println("  Type length: " + type.length());
						System.out.println("  Type char array: " + Arrays.toString(type.toCharArray()));
						System.out.println("  Equals 'class': " + type.equals("class"));
						System.out.println("  Trimmed equals 'class': " + type.trim().equals("class"));

						if (type.contains("class")) {
							System.out.println("  ⚠️  CONTAINS 'class' but doesn't equal it!");
							System.out.println("  Index of 'class': " + type.indexOf("class"));
						}
					}
				}
			}

			// Now try deserialization
			System.out.println("\n=== Attempting Deserialization ===");
			Result<Lang.JRoot, CompileError> deserResult = JavaSerializer.deserialize(Lang.JRoot.class, lexedNode);

			if (deserResult instanceof Err<Lang.JRoot, CompileError>(CompileError error)) {
				System.err.println("❌ Deserialization failed:");
				System.err.println(error.display());
				// Don't fail the test - we expect this to fail
			} else System.out.println("✅ Deserialization succeeded!");
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}
