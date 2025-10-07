import magma.compile.CRules;
import magma.compile.JavaSerializer;
import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.list.List;
import magma.list.NonEmptyList;
import magma.option.Option;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

public class TestFunctionSerialization {
	@Test
	public void testSerializeFunction() {
		// Create a simple Function with a body (list of CFunctionSegment)
		List<Lang.CFunctionSegment> body = List.of(new Lang.Placeholder("/*test body*/"));
		Lang.Function func = new Lang.Function(
				new Lang.CDefinition("testFunc", new Lang.Identifier("void"), Option.empty()),
				Option.empty(),
				NonEmptyList.from(body),
				new Some<String>(System.lineSeparator()),
				Option.empty());

		System.out.println("Created function with body size: " + func.body().size());

		// Serialize it
		Result<Node, CompileError> serResult = JavaSerializer.serialize(Lang.Function.class, func);
		if (serResult instanceof Ok<Node, ?>(Node node)) {
			System.out.println("Serialization successful");
			System.out.println("Node: " + node);

			// Try to generate it
			Result<String, CompileError> genResult = CRules.CFunction().generate(node);
			if (genResult instanceof Ok<String, ?>(String text)) {
				System.out.println("Generation successful");
				System.out.println("Generated text length: " + text.length());
				System.out.println("Generated text: " + text);
			} else
				System.out.println("Generation failed: " + genResult);
		} else
			System.out.println("Serialization failed: " + serResult);
	}
}
