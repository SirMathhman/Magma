import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

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

		Result<Node, CompileError> lexResult = Lang.JRoot().lex(code); if (lexResult instanceof Ok<Node, ?>(Node node)) {
			Result<Lang.JavaRoot, CompileError> deserResult = Serialize.deserialize(Lang.JavaRoot.class, node);
			if (deserResult instanceof Ok<Lang.JavaRoot, ?>(Lang.JavaRoot root))
				for (Lang.JavaRootSegment child : root.children()) {
					if (child instanceof Lang.JClass jClass) for (Lang.JStructureSegment member : jClass.children()) {
						if (member instanceof Lang.Method method) {
							System.out.println("=== Method: " + method.definition().name() + " ===");
							System.out.println("Body: " + method.body());

							if (method.body() instanceof Some<?>(Object segments)) {
								System.out.println("Segments count: " + ((List<?>) segments).size());
								for (Object segment : (List<?>) segments) {
									System.out.println("  Segment type: " + segment.getClass().getSimpleName());
									if (segment instanceof Lang.Placeholder(String value))
										System.out.println("  Placeholder value: " + value);
								}
							}

							// Test the transformation logic
							final String bodyString = switch (method.body()) {
								case None<List<Lang.JFunctionSegment>> _ -> "";
								case Some<List<Lang.JFunctionSegment>>(List<Lang.JFunctionSegment> segs) -> {
									StringBuilder sb = new StringBuilder(); for (Lang.JFunctionSegment segment : segs) {
										if (segment instanceof Lang.Placeholder(String value)) sb.append("/*").append(value).append("*/");
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
