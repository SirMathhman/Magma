package magma;

import magma.compile.JavaSerializer;
import magma.compile.error.CompileError;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static magma.compile.Lang.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

		Result<magma.compile.Node, CompileError> lexResult = JRoot().lex(input);
		assertTrue(lexResult instanceof Ok<?, ?>, () -> "Lexing failed: " + lexResult);

		if (lexResult instanceof Ok<magma.compile.Node, CompileError> lexOk) {
			magma.compile.Node rootNode = lexOk.value();

			// Find the record node
			if (rootNode.findNodeList("children") instanceof Some<?> some) {
				@SuppressWarnings("unchecked")
				List<magma.compile.Node> children = (List<magma.compile.Node>) some.value();

				children.forEach(child -> {
					if (child.is("record")) {
						System.out.println("Found record node!");

						// Get the record's children (should include the method)
						if (child.findNodeList("children") instanceof Some<?> recordChildrenSome) {
							@SuppressWarnings("unchecked")
							List<magma.compile.Node> recordChildren = (List<magma.compile.Node>) recordChildrenSome.value();

							System.out.println("Record has " + recordChildren.size() + " children:");
							recordChildren.forEach(recordChild -> {
								System.out.println("  Child @type: " + recordChild.maybeType);

								if (recordChild.is("method")) {
									System.out.println("  Found method node! Trying to deserialize...");

									// Try to deserialize as Method directly
									Result<Method, CompileError> methodResult = JavaSerializer.deserialize(Method.class, recordChild);
									if (methodResult instanceof Ok<Method, CompileError> methodOk) {
										System.out.println("  ✅ Method deserialization SUCCESS");
										System.out.println("    Method name: " + methodOk.value().definition().name());
										System.out.println("    Method body: " + methodOk.value().body());
									} else if (methodResult instanceof Err<Method, CompileError> methodErr) {
										System.out.println("  ❌ Method deserialization FAILED: " + methodErr.error());
									}

									// Try to deserialize as JavaStructureSegment interface
									Result<JStructureSegment, CompileError> segmentResult =
											JavaSerializer.deserialize(JStructureSegment.class, recordChild);
									if (segmentResult instanceof Ok<JStructureSegment, CompileError> segmentOk) {
										System.out.println("  ✅ JavaStructureSegment deserialization SUCCESS");
										System.out.println("    Segment actual type: " + segmentOk.value().getClass().getSimpleName());
									} else if (segmentResult instanceof Err<JStructureSegment, CompileError> segmentErr) {
										System.out.println("  ❌ JavaStructureSegment deserialization FAILED: " + segmentErr.error());
									}
								}
							});
						}
					}
				});
			}
		}
	}
}