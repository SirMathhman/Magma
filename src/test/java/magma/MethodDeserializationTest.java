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
		assertInstanceOf(Ok.class, lexResult, () -> "Lexing failed: " + lexResult);

		Ok<Node, CompileError> nodeCompileErrorOk = (Ok<Node, CompileError>) lexResult;
		Node value = nodeCompileErrorOk.value();// Find the record node
		if (value.findNodeList("children") instanceof Some(Object childrenValue)) {
			@SuppressWarnings("unchecked")
			List<Node> children = (List<Node>) childrenValue;

			children.stream().forEach(child -> {
				if (child.is("record")) {
					System.out.println("Found record node!");

					// Get the record's children (should include the method)
					if (child.findNodeList("children") instanceof Some(Object recordChildrenValue)) {
						@SuppressWarnings("unchecked")
						List<Node> recordChildren = (List<Node>) recordChildrenValue;
						System.out.println("Record has " + recordChildren.size() + " children:");
						recordChildren.stream().forEach(recordChild -> {
							System.out.println("  Child @type: " + recordChild.maybeType);

							if (recordChild.is("method")) {
								System.out.println("  Found method node! Trying to deserialize...");

								// Try to deserialize as Method directly
								Result<Method, CompileError> methodResult = JavaSerializer.deserialize(Method.class, recordChild);
								if (methodResult instanceof Ok<Method, CompileError>(Method method)) {
									System.out.println("  ✅ Method deserialization SUCCESS");
									System.out.println("    Method name: " + method.definition().name());
									System.out.println("    Method body: " + method.body());
								} else if (methodResult instanceof Err<Method, CompileError>(CompileError error))
									System.out.println("  ❌ Method deserialization FAILED: " + error);

								// Try to deserialize as JavaStructureSegment interface
								Result<JStructureSegment, CompileError> segmentResult = JavaSerializer
										.deserialize(JStructureSegment.class, recordChild);
								if (segmentResult instanceof Ok<JStructureSegment, CompileError>(JStructureSegment segment)) {
									System.out.println("  ✅ JavaStructureSegment deserialization SUCCESS");
									System.out.println("    Segment actual type: " + segment.getClass().getSimpleName());
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