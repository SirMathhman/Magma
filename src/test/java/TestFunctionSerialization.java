import magma.compile.CRules;
import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.option.Option;
import magma.result.Ok;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestFunctionSerialization {
	@Test
	public void testSerializeFunction() {
		// Create a simple Function with a body (list of CFunctionSegment)
		List<Lang.CFunctionSegment> body = java.util.List.of(new Lang.Placeholder("/*test body*/")); var func =
				new Lang.Function(new Lang.CDefinition("testFunc", new Lang.Identifier("void"), magma.option.Option.empty()),
													java.util.Collections.emptyList(),
													body,
													new Option.Some<>(System.lineSeparator()),
													magma.option.Option.empty());

		System.out.println("Created function with body size: " + func.body().size());

		// Serialize it
		var serResult = JavaSerializer.serialize(Lang.Function.class, func); if (serResult instanceof Ok<Node, ?>(var node)) {
			System.out.println("Serialization successful"); System.out.println("Node: " + node);

			// Try to generate it
			var genResult = CRules.CFunction().generate(node); if (genResult instanceof Ok<String, ?>(var text)) {
				System.out.println("Generation successful");
				System.out.println("Generated text length: " + ((String) text).length());
				System.out.println("Generated text: " + text);
			} else {
				System.out.println("Generation failed: " + genResult);
			}
		} else {
			System.out.println("Serialization failed: " + serResult);
		}
	}
}
