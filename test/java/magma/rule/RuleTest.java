package magma.rule;

import magma.error.CompileError;
import magma.input.Input;
import magma.input.RootInput;
import magma.node.Node;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Rule classes to verify they work correctly with the Input interface.
 */
public class RuleTest {

	// Simple test node implementation for testing
	private static class TestNode implements Node {
		private final String content;

		TestNode(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}

		@Override
		public Optional<String> type() {
			return Optional.empty();
		}

		@Override
		public Node retype(String type) {
			return this;
		}

		@Override
		public Optional<String> findString(String key) {
			return Optional.empty();
		}

		@Override
		public Node withString(String key, String value) {
			return this;
		}

		@Override
		public Optional<List<Node>> findNodeList(String key) {
			return Optional.empty();
		}

		@Override
		public Node withNodeList(String key, List<Node> value) {
			return this;
		}

		@Override
		public Node merge(Node other) {
			return this;
		}

		@Override
		public boolean is(String type) {
			return false;
		}

		@Override
		public String display() {
			return content;
		}
	}

	@Test
	void testPrefixRule() {
		// Create a simple rule that just returns the input content as a node
		Rule contentRule = new Rule() {
			@Override
			public Result<String, CompileError> generate(Node node) {
				return new Ok<>(node.toString());
			}

			@Override
			public Result<Node, CompileError> lex(Input input) {
				return new Ok<>(new TestNode(input.getContent()));
			}
		};

		// Create a prefix rule with the content rule
		PrefixRule prefixRule = new PrefixRule("Prefix", contentRule);

		// Test lexing with a valid input
		Input validInput = new RootInput("PrefixContent"); Result<Node, CompileError> result = prefixRule.lex(validInput);

		assertTrue(result.isOk()); assertEquals("Content", result.unwrap().toString());

		// Test lexing with an invalid input
		Input invalidInput = new RootInput("InvalidContent"); result = prefixRule.lex(invalidInput);

		assertTrue(result.isErr());
	}

	@Test
	void testSuffixRule() {
		// Create a simple rule that just returns the input content as a node
		Rule contentRule = new Rule() {
			@Override
			public Result<String, CompileError> generate(Node node) {
				return new Ok<>(node.toString());
			}

			@Override
			public Result<Node, CompileError> lex(Input input) {
				return new Ok<>(new TestNode(input.getContent()));
			}
		};

		// Create a suffix rule with the content rule
		SuffixRule suffixRule = new SuffixRule(contentRule, "Suffix");

		// Test lexing with a valid input
		Input validInput = new RootInput("ContentSuffix"); Result<Node, CompileError> result = suffixRule.lex(validInput);

		assertTrue(result.isOk()); assertEquals("Content", result.unwrap().toString());

		// Test lexing with an invalid input
		Input invalidInput = new RootInput("InvalidContent"); result = suffixRule.lex(invalidInput);

		assertTrue(result.isErr());
	}

	@Test
	void testInfixRule() {
		// Create simple rules that just return the input content as a node
		Rule leftRule = new Rule() {
			@Override
			public Result<String, CompileError> generate(Node node) {
				return new Ok<>(node.toString());
			}

			@Override
			public Result<Node, CompileError> lex(Input input) {
				return new Ok<>(new TestNode(input.getContent()));
			}
		};

		Rule rightRule = new Rule() {
			@Override
			public Result<String, CompileError> generate(Node node) {
				return new Ok<>(node.toString());
			}

			@Override
			public Result<Node, CompileError> lex(Input input) {
				return new Ok<>(new TestNode(input.getContent()));
			}
		};

		// Create an infix rule with the left and right rules
		InfixRule infixRule = new InfixRule(leftRule, "-Infix-", rightRule);

		// Test lexing with a valid input
		Input validInput = new RootInput("Left-Infix-Right");
		Result<Node, CompileError> result = infixRule.lex(validInput);

		assertTrue(result.isOk());

		// Test lexing with an invalid input
		Input invalidInput = new RootInput("InvalidContent"); result = infixRule.lex(invalidInput);

		assertTrue(result.isErr());
	}
}