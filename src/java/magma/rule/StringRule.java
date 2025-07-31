package magma.rule;

import magma.error.CompileError;
import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Optional;

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