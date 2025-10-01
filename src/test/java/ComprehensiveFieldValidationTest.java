import magma.compile.Node;
import magma.compile.Serialize;
import magma.compile.Tag;
import magma.compile.error.CompileError;
import magma.option.Option;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.List;

/**
 * Comprehensive test for the field consumption validation feature.
 * Demonstrates various scenarios where leftover fields should and should not
 * cause errors.
 */
public class ComprehensiveFieldValidationTest {

	@Tag("Person")
	public record Person(String name, String email) {}

	@Tag("Employee")
	public record Employee(String name, String department, Option<String> title) {}

	@Tag("Team")
	public record Team(String name, List<Person> members) {}

	public static void main(String[] args) {
		ComprehensiveFieldValidationTest test = new ComprehensiveFieldValidationTest(); test.testPerfectMatch();
		test.testWithOptionalFields(); test.testWithListFields(); test.testNestedStructuresWithLeftovers();
		test.testMixedScenarios();
	}

	/**
	 * Test perfect 1-1 correspondence between Node and ADT.
	 */
	public void testPerfectMatch() {
		System.out.println("=== Test: Perfect Match ===");

		Node node = new Node().retype("Person").withString("name", "Alice").withString("email", "alice@example.com");

		Result<Person, CompileError> result = Serialize.deserialize(Person.class, node);

		if (result instanceof Ok<Person, CompileError>(Person person)) {
			System.out.println("✓ SUCCESS: Perfect match - " + person);
		} else {
			System.out.println("✗ UNEXPECTED FAILURE: " + ((Err<Person, CompileError>) result).error().display());
		} System.out.println();
	}

	/**
	 * Test with optional fields - empty optionals should not cause leftover field
	 * errors.
	 */
	public void testWithOptionalFields() {
		System.out.println("=== Test: Optional Fields ===");

		// Test 1: Without optional field
		Node node1 = new Node().retype("Employee").withString("name", "Bob").withString("department", "Engineering");

		Result<Employee, CompileError> result1 = Serialize.deserialize(Employee.class, node1);

		if (result1 instanceof Ok<Employee, CompileError>(Employee employee)) {
			System.out.println("✓ SUCCESS: Optional field absent - " + employee);
		} else {
			System.out.println("✗ UNEXPECTED FAILURE: " + ((Err<Employee, CompileError>) result1).error().display());
		}

		// Test 2: With optional field
		Node node2 = new Node().retype("Employee")
													 .withString("name", "Carol")
													 .withString("department", "Marketing")
													 .withString("title", "Senior Manager");

		Result<Employee, CompileError> result2 = Serialize.deserialize(Employee.class, node2);

		if (result2 instanceof Ok<Employee, CompileError>(Employee employee)) {
			System.out.println("✓ SUCCESS: Optional field present - " + employee);
		} else {
			System.out.println("✗ UNEXPECTED FAILURE: " + ((Err<Employee, CompileError>) result2).error().display());
		}

		// Test 3: With extra field that should cause error
		Node node3 = new Node().retype("Employee")
													 .withString("name", "Dave")
													 .withString("department", "Sales")
													 .withString("title", "Director")
													 .withString("salary", "100000"); // This should cause an error

		Result<Employee, CompileError> result3 = Serialize.deserialize(Employee.class, node3);

		if (result3 instanceof Err<Employee, CompileError>(CompileError error)) {
			System.out.println("✓ EXPECTED FAILURE: Extra field detected");
			System.out.println("  Error summary: " + error.reason());
		} else {
			System.out.println("✗ UNEXPECTED SUCCESS: Should have failed due to extra 'salary' field");
		} System.out.println();
	}

	/**
	 * Test with list fields.
	 */
	public void testWithListFields() {
		System.out.println("=== Test: List Fields ===");

		// Create person nodes for the list
		Node person1 = new Node().retype("Person").withString("name", "Alice").withString("email", "alice@example.com");

		Node person2 = new Node().retype("Person").withString("name", "Bob").withString("email", "bob@example.com");

		Node teamNode = new Node().retype("Team")
															.withString("name", "Development Team")
															.withNodeList("members", List.of(person1, person2));

		Result<Team, CompileError> result = Serialize.deserialize(Team.class, teamNode);

		if (result instanceof Ok<Team, CompileError>(Team team)) {
			System.out.println("✓ SUCCESS: List field consumed - " + team);
		} else {
			System.out.println("✗ UNEXPECTED FAILURE: " + ((Err<Team, CompileError>) result).error().display());
		} System.out.println();
	}

	/**
	 * Test nested structures where inner nodes have leftover fields.
	 * The validation should only check the top-level fields.
	 */
	public void testNestedStructuresWithLeftovers() {
		System.out.println("=== Test: Nested Structures with Inner Leftovers ===");

		// Create a person node with an extra field
		Node personWithExtra = new Node().retype("Person")
																		 .withString("name", "Charlie")
																		 .withString("email", "charlie@example.com")
																		 .withString("phone", "555-1234"); // Extra field in nested object

		Node teamNode =
				new Node().retype("Team").withString("name", "QA Team").withNodeList("members", List.of(personWithExtra));

		Result<Team, CompileError> result = Serialize.deserialize(Team.class, teamNode);

		// This should fail because the nested Person has a leftover field
		if (result instanceof Err<Team, CompileError>(CompileError error)) {
			System.out.println("✓ EXPECTED FAILURE: Nested object has leftover fields");
			System.out.println("  Error summary: " + error.reason());
		} else {
			System.out.println("✗ UNEXPECTED SUCCESS: Should have failed due to nested leftover field");
		} System.out.println();
	}

	/**
	 * Test mixed scenarios with multiple types of leftover field issues.
	 */
	public void testMixedScenarios() {
		System.out.println("=== Test: Mixed Scenarios ===");

		// Node with multiple types of leftover fields
		Node complexNode = new Node().retype("Person")
																 .withString("name", "Eve")
																 .withString("email", "eve@example.com")
																 .withString("address", "123 Main St") // Leftover string field
																 .withNode("preferences",
																					 new Node().withString("theme", "dark")) // Leftover nested object
																 .withNodeList("tags", List.of(new Node().withString("tag", "vip"))); // Leftover list

		Result<Person, CompileError> result = Serialize.deserialize(Person.class, complexNode);

		if (result instanceof Err<Person, CompileError>(CompileError error)) {
			System.out.println("✓ EXPECTED FAILURE: Multiple leftover fields detected");
			System.out.println("  Error summary: " + error.reason());
		} else {
			System.out.println("✗ UNEXPECTED SUCCESS: Should have failed due to multiple leftover fields");
		} System.out.println();
	}
}