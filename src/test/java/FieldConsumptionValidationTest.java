import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.Tag;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

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

	public static void main(String[] args) {
		FieldConsumptionValidationTest test = new FieldConsumptionValidationTest(); test.testSuccessfulDeserialization();
		test.testLeftoverFieldsDetection(); test.testExtraFieldsInCompleteDeserialization();
	}

	/**
	 * Test that a Node with exactly the right fields deserializes successfully.
	 */
	public void testSuccessfulDeserialization() {
		System.out.println("=== Test: Successful Deserialization ===");

		// Create a Node with exactly the fields needed for TestRecord
		Node node = new Node().retype("TestRecord").withString("name", "John").withString("age", "25");

		Result<TestRecord, CompileError> result = Serialize.deserialize(TestRecord.class, node);

		if (result instanceof Ok<TestRecord, CompileError>(TestRecord record)) {
			System.out.println("✓ SUCCESS: Deserialized successfully"); System.out.println("  Record: " + record);
		} else if (result instanceof Err<TestRecord, CompileError>(CompileError error)) {
			System.out.println("✗ FAILED: " + error.display());
		} System.out.println();
	}

	/**
	 * Test that leftover fields are detected and cause an error.
	 */
	public void testLeftoverFieldsDetection() {
		System.out.println("=== Test: Leftover Fields Detection ===");

		// Create a Node with more fields than the target record can consume
		Node node = new Node().retype("PartialRecord")
													.withString("name", "John")
													.withString("age", "25") // This field won't be consumed by PartialRecord
													.withString("email", "john@example.com"); // This field won't be consumed either

		Result<PartialRecord, CompileError> result = Serialize.deserialize(PartialRecord.class, node);

		if (result instanceof Ok<PartialRecord, CompileError>(PartialRecord record)) {
			System.out.println("✗ UNEXPECTED SUCCESS: Should have failed due to leftover fields");
			System.out.println("  Record: " + record);
		} else if (result instanceof Err<PartialRecord, CompileError>(CompileError error)) {
			System.out.println("✓ EXPECTED FAILURE: Leftover fields detected");
			System.out.println("  Error: " + error.display());
		} System.out.println();
	}

	/**
	 * Test with a complex Node structure that has nested objects and lists.
	 */
	public void testExtraFieldsInCompleteDeserialization() {
		System.out.println("=== Test: Extra Fields in Complex Structure ===");

		// Create a Node that looks like TestRecord but has extra nested data
		Node nestedNode = new Node().withString("extra", "This shouldn't be here");

		Node node = new Node().retype("TestRecord")
													.withString("name", "Jane")
													.withString("age", "30")
													.withNode("profile", nestedNode) // Extra nested object
													.withString("department", "Engineering"); // Extra string field

		Result<TestRecord, CompileError> result = Serialize.deserialize(TestRecord.class, node);

		if (result instanceof Ok<TestRecord, CompileError>(TestRecord record)) {
			System.out.println("✗ UNEXPECTED SUCCESS: Should have failed due to extra fields");
			System.out.println("  Record: " + record);
		} else if (result instanceof Err<TestRecord, CompileError>(CompileError error)) {
			System.out.println("✓ EXPECTED FAILURE: Extra fields detected");
			System.out.println("  Error: " + error.display());
		} System.out.println();
	}
}