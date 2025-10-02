package magma;

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

public class MethodDeserializationTest {

	@Test
	public void testMethodDeserializationDirectly() {
		String input = """
				public record Some<T>(T value) {
				    public T getValue() {
				        return value;
				    }
				}
				""";

		System.out.println("=== Testing Method Deserialization ===");

		Result<Node, CompileError> lexResult = JRoot().lex(input);
		assertInstanceOf(Ok<?, ?>.class, lexResult, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<Node, CompileError>(Node value)) {

			// Find the record node
			if (value.findNodeList("children") instanceof Some<?>(? value)) {
				@SuppressWarnings("unchecked")
				List<Node> children = (List<Node>) value;

				children.forEach(child -> {
					if (child.is("record")) {
						System.out.println("Found record node!");

						// Get the record's children (should include the method)
						if (child.findNodeList("children") instanceof Some<?>(? value)) {
							@SuppressWarnings("unchecked")
							List<Node> recordChildren = (List<Node>) value;

							System.out.println("Record has " + recordChildren.size() + " children:");
							recordChildren.forEach(recordChild -> {
								System.out.println("  Child @type: " + recordChild.maybeType);

								if (recordChild.is("method")) {
									System.out.println("  Found method node! Trying to deserialize...");

									// Try to deserialize as Method directly
									Result<Method, CompileError> methodResult = Serialize.deserialize(Method.class, recordChild);
									if (methodResult instanceof Ok<Method, CompileError>(Method value)) {
										System.out.println("  ✅ Method deserialization SUCCESS");
										System.out.println("    Method name: " + value.definition().name());
										System.out.println("    Method body: " + value.body());
									} else if (methodResult instanceof Err<Method, CompileError>(CompileError error))
										System.out.println("  ❌ Method deserialization FAILED: " + error);

									// Try to deserialize as JavaStructureSegment interface
									Result<JStructureSegment, CompileError> segmentResult =
											Serialize.deserialize(JStructureSegment.class, recordChild);
									if (segmentResult instanceof Ok<JStructureSegment, CompileError>(JStructureSegment value)) {
										System.out.println("  ✅ JavaStructureSegment deserialization SUCCESS");
										System.out.println("    Segment actual type: " + value.getClass().getSimpleName());
									} else if (segmentResult instanceof Err<JStructureSegment, CompileError>(CompileError error))
										System.out.println("  ❌ JavaStructureSegment deserialization FAILED: " + error);
								}
							});
						}
					}
				});
			}
		}
	}
}