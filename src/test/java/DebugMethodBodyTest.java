import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class DebugMethodBodyTest {
	@Test
	public void testMethodBodyStatements() {
		String code = """
				public class TestClass {
					public void method() {
						System.out.println("Hello");
						int x = 5;
					}
				}
				""";

		System.out.println("=== Parsing Java code ===");
		Result<Node, ?> lexResult = Lang.JRoot().lex(code);
		if (lexResult instanceof Err<?, ?>(Object err)) {
			fail("Lex failed: " + err);
			return;
		}
		Node lexed = ((Ok<Node, ?>) lexResult).value();
		System.out.println("Lexed successfully");

		System.out.println("\n=== Deserializing ===");
		Result<Lang.JRoot, ?> result = JavaSerializer.deserialize(Lang.JRoot.class, lexed);

		if (result instanceof Ok<Lang.JRoot, ?>(Lang.JRoot root)) {
			System.out.println("✅ Deserialization successful");
			System.out.println("JavaRoot children: " + root.children().size());

			processRootChildren(root);
		} else if (result instanceof Err<?, ?>(Object err)) {
			System.out.println("❌ Deserialization failed: " + err);
			fail("Deserialization should succeed");
		}
	}

	private void processRootChildren(Lang.JRoot root) {
		for (int i = 0; i < root.children().size(); i++) {
			Lang.JavaRootSegment child = root.children().getOrNull(i);
			if (child instanceof Lang.JClass jClass) {
				System.out.println("\n=== Found JClass: " + jClass.name() + " ===");
				System.out.println("JClass children: " + jClass.children().size());
				processClassChildren(jClass);
			}
		}
	}

	private void processClassChildren(Lang.JClass jClass) {
		for (int j = 0; j < jClass.children().size(); j++) {
			Lang.JStructureSegment member = jClass.children().getOrNull(j);
			if (member instanceof Lang.Method method) {
				System.out.println("\n=== Found Method ===");
				System.out.println("Method definition: " + method.definition());
				System.out.println("Body present: " + (method.body() instanceof Some));

				if (method.body() instanceof Some<?>(Object body)) {
					System.out.println("Body type: " + body.getClass().getName());
					System.out.println("Body content: " + body);

					if (body instanceof List<?> list) {
						System.out.println("Body list size: " + list.size());
						printBodyList(list);
					}
				} else System.out.println("❌ Body is None!");
			}
		}
	}

	private void printBodyList(List<?> list) {
		for (int k = 0; k < list.size(); k++) {
			Object item = list.get(k);
			System.out.println("  [" + k + "] Type: " + item.getClass().getSimpleName() + ", Value: " + item);
		}
	}
}
