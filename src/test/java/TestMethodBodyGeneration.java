import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.option.Some;
import magma.result.Ok;
import org.junit.jupiter.api.Test;

public class TestMethodBodyGeneration {
	@Test
	public void testBodyGeneration() {
		String code = """
				public class TestClass {
					public void method() {
						System.out.println("Hello");
						int x = 5;
					}
				}
				""";

		var lexResult = Lang.JRoot().lex(code); if (lexResult instanceof Ok<Node, ?>(var node)) {
			var deserResult = Serialize.deserialize(Lang.JavaRoot.class, node);
			if (deserResult instanceof Ok<Lang.JavaRoot, ?>(var root)) {
				for (var child : root.children()) {
					if (child instanceof Lang.JClass jClass) {
						for (var member : jClass.children()) {
							if (member instanceof Lang.Method method) {
								System.out.println("=== Method: " + method.definition().name() + " ===");
								System.out.println("Body: " + method.body());

								if (method.body() instanceof Some<?>(var segments)) {
									System.out.println("Segments count: " + ((java.util.List<?>) segments).size());
									for (var segment : (java.util.List<?>) segments) {
										System.out.println("  Segment type: " + segment.getClass().getSimpleName());
										if (segment instanceof Lang.Placeholder placeholder) {
											System.out.println("  Placeholder value: " + placeholder.value());
										}
									}
								}

								// Test the transformation logic
								final String bodyString = switch (method.body()) {
									case magma.option.None<java.util.List<Lang.JMethodSegment>> _ -> "";
									case Some<java.util.List<Lang.JMethodSegment>>(var segs) -> {
										StringBuilder sb = new StringBuilder(); for (Lang.JMethodSegment segment : segs) {
											if (segment instanceof Lang.Placeholder placeholder) {
												sb.append("/*").append(placeholder.value()).append("*/");
											}
										} yield sb.toString();
									}
								};

								System.out.println("Generated body string length: " + bodyString.length());
								System.out.println("Generated body: " + bodyString);
							}
						}
					}
				}
			}
		}
	}
}
