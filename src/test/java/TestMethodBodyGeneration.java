import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.list.List;
import magma.option.None;
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

		var lexResult = Lang.JRoot().lex(code);
		if (lexResult instanceof Ok<Node, ?>(var node)) {
			var deserResult = JavaSerializer.deserialize(Lang.JRoot.class, node);
			if (deserResult instanceof Ok<Lang.JRoot, ?>(var root)) {
				for (int i = 0; i < root.children().size(); i++) {
					var child = root.children().getOrNull(i);
					if (child instanceof Lang.JClass jClass) {
						for (int j = 0; j < jClass.children().size(); j++) {
							var member = jClass.children().getOrNull(j);
							if (member instanceof Lang.Method method) {
								System.out.println("=== Method: " + method.definition().name() + " ===");
								System.out.println("Body: " + method.body());

								if (method.body() instanceof Some<?>(var segments)) {
									System.out.println("Segments count: " + ((java.util.List<?>) segments).size());
									var segList = (java.util.List<?>) segments;
									for (int k = 0; k < segList.size(); k++) {
										var segment = segList.get(k);
										System.out.println("  Segment type: " + segment.getClass().getSimpleName());
										if (segment instanceof Lang.Placeholder placeholder) {
											System.out.println("  Placeholder value: " + placeholder.value());
										}
									}
								}

								// Test the transformation logic
								final String bodyString = switch (method.body()) {
									case None<List<Lang.JMethodSegment>> _ -> "";
									case Some<List<Lang.JMethodSegment>>(var segs) -> {
										StringBuilder sb = new StringBuilder();
										for (int m = 0; m < segs.size(); m++) {
											Lang.JMethodSegment segment = segs.getOrNull(m);
											if (segment instanceof Lang.Placeholder placeholder) {
												sb.append("/*").append(placeholder.value()).append("*/");
											}
										}
										yield sb.toString();
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
