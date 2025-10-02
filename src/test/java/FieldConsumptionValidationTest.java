import magma.compile.JavaSerializer;
import magma.compile.Node;
import magma.compile.Tag;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for the new field consumption validation feature.
 * This ensures that there's a 1-1 correspondence between Node fields and ADT
 * fields.
 */
public class FieldConsumptionValidationTest {

	@Tag("TestRecord")
	public record TestRecord(String name, String age) {}

	@Tag("PartialRecord")
	public record PartialRecord(String name) {}

	// Tests migrated to JUnit 5

	/**
	 * Test that a Node with exactly the right fields deserializes successfully.
	 */
	@Test
	public void testSuccessfulDeserialization() {
		System.out.println("=== Test: Successful Deserialization ===");

		// Create a Node with exactly the fields needed for TestRecord
		Node node = new Node().retype("TestRecord").withString("name", "John").withString("age", "25");

		Result<TestRecord, CompileError> result = JavaSerializer.deserialize(TestRecord.class, node);
		assertTrue(result instanceof Ok<?, ?>, () -> "Expected Ok but got: " + result);
	}

	/**
	 * Test that leftover fields are detected and cause an error.
	 */
	@Test
	public void testLeftoverFieldsDetection() {
		System.out.println("=== Test: Leftover Fields Detection ===");

		// Create a Node with more fields than the target record can consume
		Node node = new Node().retype("PartialRecord")
													.withString("name", "John")
													.withString("age", "25") // This field won't be consumed by PartialRecord
													.withString("email", "john@example.com"); // This field won't be consumed either

		Result<PartialRecord, CompileError> result = JavaSerializer.deserialize(PartialRecord.class, node);
		assertTrue(result instanceof Err<?, ?>, () -> "Expected Err due to leftover fields but got: " + result);
	}

	/**
	 * Test with a complex Node structure that has nested objects and lists.
	 */
	@Test
	public void testExtraFieldsInCompleteDeserialization() {
		System.out.println("=== Test: Extra Fields in Complex Structure ===");

		// Create a Node that looks like TestRecord but has extra nested data
		Node nestedNode = new Node().withString("extra", "This shouldn't be here");

		Node node = new Node().retype("TestRecord")
													.withString("name", "Jane")
													.withString("age", "30")
													.withNode("profile", nestedNode) // Extra nested object
													.withString("department", "Engineering"); // Extra string field

		Result<TestRecord, CompileError> result = JavaSerializer.deserialize(TestRecord.class, node);
		assertTrue(result instanceof Err<?, ?>, () -> "Expected Err due to extra fields but got: " + result);
	}
}