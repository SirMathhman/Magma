import magma.compile.JavaSerializer;
import magma.compile.Node;
import magma.compile.Tag;
import magma.compile.error.CompileError;
import magma.list.List;
import magma.option.Option;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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

	@Test
	public void testPerfectMatch() {
		Node node = new Node().retype("Person").withString("name", "Alice").withString("email", "alice@example.com");

		Result<Person, CompileError> result = JavaSerializer.deserialize(Person.class, node);
		assertInstanceOf(Ok<?, ?>.class, result, () -> "Expected successful deserialization but got: " + result);
	}

	@Test
	public void testWithOptionalFields() {
		// Test 1: Without optional field
		Node node1 = new Node().retype("Employee").withString("name", "Bob").withString("department", "Engineering");

		Result<Employee, CompileError> result1 = JavaSerializer.deserialize(Employee.class, node1);
		assertInstanceOf(Ok<?, ?>.class, result1, () -> "Expected Ok when optional absent but got: " + result1);

		// Test 2: With optional field
		Node node2 = new Node().retype("Employee")
													 .withString("name", "Carol")
													 .withString("department", "Marketing")
													 .withString("title", "Senior Manager");

		Result<Employee, CompileError> result2 = JavaSerializer.deserialize(Employee.class, node2);
		assertInstanceOf(Ok<?, ?>.class, result2, () -> "Expected Ok when optional present but got: " + result2);

		// Test 3: With extra field that should cause error
		Node node3 = new Node().retype("Employee")
													 .withString("name", "Dave")
													 .withString("department", "Sales")
													 .withString("title", "Director")
													 .withString("salary", "100000"); // This should cause an error

		Result<Employee, CompileError> result3 = JavaSerializer.deserialize(Employee.class, node3);
		assertInstanceOf(Err<?, ?>.class, result3, () -> "Expected Err due to extra field 'salary' but got: " + result3);
	}

	@Test
	public void testWithListFields() {
		Node person1 = new Node().retype("Person").withString("name", "Alice").withString("email", "alice@example.com");
		Node person2 = new Node().retype("Person").withString("name", "Bob").withString("email", "bob@example.com");

		Node teamNode = new Node().retype("Team")
															.withString("name", "Development Team")
															.withNodeList("members", List.of(person1, person2));

		Result<Team, CompileError> result = JavaSerializer.deserialize(Team.class, teamNode);
		assertInstanceOf(Ok<?, ?>.class, result, () -> "Expected Ok when list fields consumed but got: " + result);
	}

	@Test
	public void testNestedStructuresWithLeftovers() {
		Node personWithExtra = new Node().retype("Person")
																		 .withString("name", "Charlie")
																		 .withString("email", "charlie@example.com")
																		 .withString("phone", "555-1234"); // Extra field in nested object

		Node teamNode =
				new Node().retype("Team").withString("name", "QA Team").withNodeList("members", List.of(personWithExtra));

		Result<Team, CompileError> result = JavaSerializer.deserialize(Team.class, teamNode);
		assertInstanceOf(Err<?, ?>.class, result, () -> "Expected Err due to nested leftover fields but got: " + result);
	}

	@Test
	public void testMixedScenarios() {
		Node complexNode = new Node().retype("Person")
																 .withString("name", "Eve")
																 .withString("email", "eve@example.com")
																 .withString("address", "123 Main St") // Leftover string field
																 .withNode("preferences",
																					 new Node().withString("theme", "dark")) // Leftover nested object
																 .withNodeList("tags", List.of(new Node().withString("tag", "vip"))); // Leftover list

		Result<Person, CompileError> result = JavaSerializer.deserialize(Person.class, complexNode);
		assertInstanceOf(Err<?, ?>.class, result, () -> "Expected Err due to multiple leftover fields but got: " + result);
	}
}