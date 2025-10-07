import magma.compile.JavaSerializer;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleClassWithMethodTest {

	@Test
	public void testClassWithSingleStaticMethod() {
		String input = """
				package test;

				public class Simple {
					public static void main(String[] args) {
						System.out.println("test");
					}
				}
				""";

		System.out.println("=== Testing class with single static method ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		if (lexResult instanceof Err<Node, CompileError>(CompileError error)) {
			System.err.println("❌ LEXING FAILED: " + error);
			fail("Lexing failed: " + error);
		}

		assertInstanceOf(Ok.class, lexResult, "Lexing should succeed");
		Node lexedNode = ((Ok<Node, CompileError>) lexResult).value();
		System.out.println("\n✅ Lexing succeeded");
		System.out.println("\nLexed structure:");
		System.out.println(lexedNode.format(0));

		Result<JRoot, CompileError> deserializeResult = JavaSerializer.deserialize(JRoot.class, lexedNode);
		if (deserializeResult instanceof Err<JRoot, CompileError>(CompileError error)) {
			System.err.println("\n❌ DESERIALIZATION FAILED: " + error);
			fail("Deserialization failed: " + error);
		}

		assertInstanceOf(Ok.class, deserializeResult, "Deserialization should succeed");
		JRoot javaRoot = ((Ok<JRoot, CompileError>) deserializeResult).value();
		System.out.println("\n✅ Deserialization succeeded");
		int childrenCount = javaRoot.children().map(nel -> nel.size()).orElse(0);
		System.out.println("JavaRoot children count: " + childrenCount);

		javaRoot.children().map(nel -> nel.toList()).orElse(new magma.list.ArrayList<>()).stream().forEach(child -> {
			System.out.println("  Child type: " + child.getClass().getSimpleName());
			if (child instanceof JClass jClass) {
				System.out.println("    Class name: " + jClass.name());
				System.out.println("    Class children count: " + jClass.children().size());
				jClass.children().stream().forEach(structChild -> {
					System.out.println("      Structure child type: " + structChild.getClass().getSimpleName());
					if (structChild instanceof Method method) {
						System.out.println("        ✅ Method found: " + method.definition().name());
						System.out.println("        Method body: " + method.body());
						System.out.println("        Method params: " + method.params());
					}
				});
			}
		});

		// Check that the class has at least one method
		boolean hasMethod = javaRoot.children()
				.map(nel -> nel.toList())
				.orElse(new magma.list.ArrayList<>())
				.stream()
				.filter(child -> child instanceof JClass)
				.map(child -> (JClass) child)
				.anyMatch(jClass -> jClass.children().stream().anyMatch(seg -> seg instanceof Method));

		assertTrue(hasMethod, "Class should contain at least one method");
	}
}
