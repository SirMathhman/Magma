package magma;

import magma.compile.Lang;
import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.error.CompileError;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SealedInterfaceDeserializationTest {

	@Test
	public void testRecordDeserializationDirectly() {
		String input = """
				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Direct Record Deserialization ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			System.out.println(
					"Root node children count: " + value.findNodeList("children").map(list -> list.size()).orElse(0));

			// Find the record node specifically
			if (value.findNodeList("children") instanceof Some<?>(? value)) {
				@SuppressWarnings("unchecked")
				List<Node> children = (List<Node>) value;
				children.forEach(child -> {
					System.out.println("Child @type: " + child.maybeType);
					if (child.is("record")) {
						System.out.println("Found record node! Trying to deserialize as Record class directly...");

						Result<Lang.Record, CompileError> recordResult = Serialize.deserialize(Lang.Record.class, child);
						if (recordResult instanceof Ok<Lang.Record, CompileError>(Lang.Record value)) {
							System.out.println("✅ Record deserialization SUCCESS");
							System.out.println("Record name: " + value.name());
							System.out.println("Record children: " + value.children().size());
						} else if (recordResult instanceof Err<Lang.Record, CompileError>(CompileError error))
							System.out.println("❌ Record deserialization FAILED: " + error);

						System.out.println("Now trying to deserialize as JStructure interface...");
						Result<JStructure, CompileError> jStructResult = Serialize.deserialize(JStructure.class, child);
						if (jStructResult instanceof Ok<JStructure, CompileError>(JStructure value)) {
							System.out.println("✅ JStructure deserialization SUCCESS");
							System.out.println("JStructure actual type: " + value.getClass().getSimpleName());
							System.out.println("JStructure name: " + value.name());
						} else if (jStructResult instanceof Err<JStructure, CompileError>(CompileError error))
							System.out.println("❌ JStructure deserialization FAILED: " + error);

						System.out.println("Finally trying to deserialize as JavaRootSegment interface...");
						Result<JavaRootSegment, CompileError> segmentResult = Serialize.deserialize(JavaRootSegment.class, child);
						if (segmentResult instanceof Ok<JavaRootSegment, CompileError>(JavaRootSegment value)) {
							System.out.println("✅ JavaRootSegment deserialization SUCCESS");
							System.out.println("Segment actual type: " + value.getClass().getSimpleName());
						} else if (segmentResult instanceof Err<JavaRootSegment, CompileError>(CompileError error))
							System.out.println("❌ JavaRootSegment deserialization FAILED: " + error);
					}
				});
			}
		}
	}

	@Test
	public void testJavaRootWithRecordDeserialization() {
		String input = """
				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing JavaRoot with Record ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {
			System.out.println("Trying to deserialize full JavaRoot...");
			Result<JavaRoot, CompileError> javaRootResult = Serialize.deserialize(JavaRoot.class, value);

			if (javaRootResult instanceof Ok<JavaRoot, CompileError>(JavaRoot value)) {
				System.out.println("✅ JavaRoot deserialization SUCCESS");
				System.out.println("JavaRoot children count: " + value.children().size()); value.children().forEach(child -> {
					System.out.println("  Child type: " + child.getClass().getSimpleName());
					if (child instanceof Lang.Record record) System.out.println("    ✅ Found Record: " + record.name());
				});
			} else if (javaRootResult instanceof Err<JavaRoot, CompileError>(CompileError error))
				System.out.println("❌ JavaRoot deserialization FAILED: " + error);
		}
	}
}