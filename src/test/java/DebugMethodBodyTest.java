import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

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

		System.out.println("=== Parsing Java code ==="); Result<Node, ?> lexResult = Lang.JRoot().lex(code);
		if (lexResult instanceof Err<?, ?>(var err)) {
			fail("Lex failed: " + err); return;
		} Node lexed = ((Ok<Node, ?>) lexResult).value(); System.out.println("Lexed successfully");

		System.out.println("\n=== Deserializing ===");
		Result<Lang.JavaRoot, ?> result = JavaSerializer.deserialize(Lang.JavaRoot.class, lexed);

		if (result instanceof Ok<Lang.JavaRoot, ?>(var root)) {
			System.out.println("✅ Deserialization successful");
			System.out.println("JavaRoot children: " + root.children().size());

			for (var child : root.children()) {
				if (child instanceof Lang.JClass jClass) {
					System.out.println("\n=== Found JClass: " + jClass.name() + " ===");
					System.out.println("JClass children: " + jClass.children().size());

					for (var member : jClass.children()) {
						if (member instanceof Lang.Method method) {
							System.out.println("\n=== Found Method ===");
							System.out.println("Method definition: " + method.definition());
							System.out.println("Body present: " + (method.body() instanceof magma.option.Some));

							if (method.body() instanceof magma.option.Some<?>(var body)) {
								System.out.println("Body type: " + body.getClass().getName());
								System.out.println("Body content: " + body);

								if (body instanceof java.util.List<?> list) {
									System.out.println("Body list size: " + list.size()); for (int i = 0; i < list.size(); i++) {
										var item = list.get(i);
										System.out.println("  [" + i + "] Type: " + item.getClass().getSimpleName() + ", Value: " + item);
									}
								}
							} else {
								System.out.println("❌ Body is None!");
							}
						}
					}
				}
			}
		} else if (result instanceof Err<?, ?>(var err)) {
			System.out.println("❌ Deserialization failed: " + err); fail("Deserialization should succeed");
		}
	}
}
