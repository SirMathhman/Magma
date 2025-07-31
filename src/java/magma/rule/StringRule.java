package magma.rule;

import magma.node.MapNode;
import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Optional;

public record StringRule(String key) implements Rule {
	@Override
	public Result<String, String> generate(final Node node) {
		Optional<String> value = node.findString(this.key()); if (value.isPresent()) {
			return new Ok<>(value.get());
		} else {
			return new Err<>("String not found for key: " + this.key());
		}
	}
	
	@Override
	public Result<Node, String> lex(final String input) {
		return new Ok<>(new MapNode().withString(this.key(), input));
	}
}