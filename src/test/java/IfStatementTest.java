import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class IfStatementTest {
	@Test
	public void testIfStatementInMethodBody() {
		String code = """
				public class TestClass {
					public void method() {
						if (true) {
							System.out.println("Hello");
						}
					}
				}
				""";

		var lexResult = Lang.JRoot().lex(code); assertTrue(lexResult instanceof Ok<?, ?>, "Lexing should succeed");

		if (lexResult instanceof Ok<Node, CompileError>(var node)) {
			System.out.println("=== Lexed node structure ==="); System.out.println(node); System.out.println();
			var deserResult = JavaSerializer.deserialize(Lang.JavaRoot.class, node);
			System.out.println("Deserialization result: " + deserResult);

			if (deserResult instanceof Ok<Lang.JavaRoot, CompileError>) {
				fail("Deserialization should have failed due to unknown 'if' tag!");
			} else if (deserResult instanceof Err<Lang.JavaRoot, CompileError>(var err)) {
				System.out.println("✅ Deserialization correctly failed!"); System.out.println("Error: " + err.display());

				// Check that the error mentions the 'if' tag
				String errorMessage = err.display(); assertTrue(errorMessage.contains("if"),
																												"Error message should mention the unknown 'if' tag. Got: " +
																												errorMessage);
			}
		}
	}
}
