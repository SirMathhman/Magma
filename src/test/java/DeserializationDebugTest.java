import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.result.Ok;
import magma.result.Result;

public class DeserializationDebugTest {

	public static void main(String[] args) {
		new DeserializationDebugTest().testMethodDeserialization();
		new DeserializationDebugTest().testSimpleMethodLexing();
	}

	public void testMethodDeserialization() {
		// Test input with a complete class structure
		String input = "class TestClass { public int add(int a, int b) { return a + b; } }";

		System.out.println("=== Testing Method Deserialization ===");
		System.out.println("Input: " + input);

		// Step 1: Parse with JavaRoot().lex()
		Result<Node, CompileError> lexResult = Lang.JavaRoot().lex(input);

		if (lexResult instanceof Ok<Node, CompileError>(Node node)) {
			System.out.println("\n=== Lexing Result ===");
			System.out.println("Node JSON:");
			System.out.println(node.format(0));

			// Step 2: Try to deserialize
			Result<Lang.JavaRoot, CompileError> deserializeResult = Serialize.deserialize(Lang.JavaRoot.class, node);

			if (deserializeResult instanceof Ok<Lang.JavaRoot, CompileError>(Lang.JavaRoot javaRoot)) {
				System.out.println("\n=== Deserialization Success ===");
				System.out.println("JavaRoot children count: " + javaRoot.children().size());

				// Check for methods in the children
				javaRoot.children().forEach(child -> {
					System.out.println("Child type: " + child.getClass().getSimpleName());

					// Need to handle this more carefully since Method is a JavaStructureSegment,
					// not JavaRootSegment
					if (child instanceof Lang.JStructure jStructure) {
						System.out.println("  JStructure found with " + jStructure.children().size() + " children");
						jStructure.children().forEach(structChild -> {
							System.out.println("    StructChild type: " + structChild.getClass().getSimpleName());
							if (structChild instanceof Lang.Method method) {
								System.out.println("    Method found!");
								System.out.println("      Definition: " + method.definition());
								System.out.println("      Params: " + method.params());
								if (method.params() instanceof magma.option.Some<?> some) {
									System.out.println("      Params content: " + some.value());
								} else {
									System.out.println("      Params is None - THIS IS THE BUG!");
								}
								System.out.println("      Body: " + method.body());
							}
						});
					}
				});
			} else {
				System.out.println("\n=== Deserialization Failed ===");
				System.out.println("Error: " + deserializeResult);
			}
		} else {
			System.out.println("Lexing failed: " + lexResult);
		}
	}

	public void testSimpleMethodLexing() {
		// Test a class with a simpler method with empty body
		String input = "class TestClass { void test(String param) {} }";

		System.out.println("\n\n=== Testing Simple Method ===");
		System.out.println("Input: " + input);

		Result<Node, CompileError> lexResult = Lang.JavaRoot().lex(input);

		if (lexResult instanceof Ok<Node, CompileError>(Node node)) {
			System.out.println("\n=== Simple Method Lexing Result ===");
			System.out.println("Node JSON:");
			System.out.println(node.format(0));
		} else {
			System.out.println("Simple method lexing failed: " + lexResult);
		}
	}
}