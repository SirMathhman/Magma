package magma.rule;

import magma.error.CompileError;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Optional;

/**
 * A simple rule implementation that stores and retrieves string values.
 * <p>
 * When lexing, this rule creates a new node with the input string stored
 * under the specified key. When generating, it looks for a string value
 * with the specified key in the node.
 * <p>
 * This rule is fundamental for capturing and generating text content
 * in the abstract syntax tree, such as identifiers, literals, and other
 * textual elements of the language.
 */
public record StringRule(String key) implements Rule {
	@Override
	public Result<String, CompileError> generate(final Node node) {
		final Optional<String> value = node.findString(this.key());
		if (value.isPresent()) {return new Ok<>(value.get());} else {
			return new Err<>(CompileError.forGeneration("String not found for key: " + this.key(), node));
		}
	}
	
	@Override
	public Result<Node, CompileError> lex(final String input) {
		return new Ok<>(new MapNode().withString(this.key(), input));
	}
}