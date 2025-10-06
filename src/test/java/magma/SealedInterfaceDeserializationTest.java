package magma;

import magma.compile.JavaSerializer;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.list.List;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

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
	assertInstanceOf(Ok.class, lexResult, () -> "Lexing failed: " + lexResult);

	Ok<Node, CompileError> nodeCompileErrorOk = (Ok<Node, CompileError>) lexResult;
		Node value = nodeCompileErrorOk.value();
	System.out.println("Root node children count: " + value.findNodeList("children").map(List::size).orElse(0));

	// Find the record node specifically
	if (value.findNodeList("children") instanceof Some(Object childrenValue)) {
		@SuppressWarnings("unchecked")
		List<Node> children = (List<Node>) childrenValue;
			children.stream().forEach(child -> {
				System.out.println("Child @type: " + child.maybeType);
				if (child.is("record")) {
					System.out.println("Found record node! Trying to deserialize as Record class directly...");

				Result<RecordNode, CompileError> recordResult = JavaSerializer
						.deserialize(RecordNode.class, child);
					if (recordResult instanceof Ok<RecordNode, CompileError>(RecordNode record)) {
						System.out.println("✅ Record deserialization SUCCESS");
						System.out.println("Record name: " + record.name());
						System.out.println("Record children: " + record.children().size());
					} else if (recordResult instanceof Err<RecordNode, CompileError>(CompileError error))
						System.out.println("❌ Record deserialization FAILED: " + error);

				System.out.println("Now trying to deserialize as JStructure interface...");
				Result<JStructure, CompileError> jStructResult = JavaSerializer.deserialize(JStructure.class, child);
				if (jStructResult instanceof Ok<JStructure, CompileError>(JStructure jStruct)) {
					System.out.println("✅ JStructure deserialization SUCCESS");
					System.out.println("JStructure actual type: " + jStruct.getClass().getSimpleName());
					System.out.println("JStructure name: " + jStruct.name());
					} else if (jStructResult instanceof Err<JStructure, CompileError>(CompileError error))
						System.out.println("❌ JStructure deserialization FAILED: " + error);

				System.out.println("Finally trying to deserialize as JavaRootSegment interface...");
				Result<JavaRootSegment, CompileError> segmentResult = JavaSerializer.deserialize(JavaRootSegment.class,
						child);
				if (segmentResult instanceof Ok<JavaRootSegment, CompileError>(JavaRootSegment segment)) {
					System.out.println("✅ JavaRootSegment deserialization SUCCESS");
					System.out.println("Segment actual type: " + segment.getClass().getSimpleName());
					} else if (segmentResult instanceof Err<JavaRootSegment, CompileError>(CompileError error))
						System.out.println("❌ JavaRootSegment deserialization FAILED: " + error);
				}
			});
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
		assertInstanceOf(Ok.class, lexResult, () -> "Lexing failed: " + lexResult);

		Ok<Node, CompileError> nodeCompileErrorOk = (Ok<Node, CompileError>) lexResult;
		Node value = nodeCompileErrorOk.value();
		System.out.println("Trying to deserialize full JavaRoot...");
		Result<JRoot, CompileError> javaRootResult = JavaSerializer.deserialize(JRoot.class, value);

		if (javaRootResult instanceof Ok<JRoot, CompileError>(JRoot javaRoot)) {
			System.out.println("✅ JavaRoot deserialization SUCCESS");
			System.out.println("JavaRoot children count: " + javaRoot.children().size());
			javaRoot.children().stream().forEach(child -> {
				System.out.println("  Child type: " + child.getClass().getSimpleName());
				if (child instanceof RecordNode record) System.out.println("    ✅ Found Record: " + record.name());
			});
		} else if (javaRootResult instanceof Err<JRoot, CompileError>(CompileError error))
			System.out.println("❌ JavaRoot deserialization FAILED: " + error);
	}
}