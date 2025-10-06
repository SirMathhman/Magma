package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.option.Option;
import magma.result.Err;
import magma.result.Result;

import java.util.List;

/**
 * A rule that generates output for a node list only if the list is non-empty.
 * Returns an error if the list is empty or missing, allowing Or rules to fall
 * back to alternatives.
 */
public record NonEmptyListRule(String key, Rule innerRule) implements Rule {

	public static Rule NonEmptyList(String key, Rule innerRule) {
		return new NonEmptyListRule(key, innerRule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		// Delegate lexing to inner rule
		return innerRule.lex(content);
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return switch (node.findNodeList(key)) {
			case Option.None<?> _ -> new Err<>(new CompileError("Node list '" + key + "' not present", new NodeContext(node)));
			case Option.Some(List<Node> list) when list.isEmpty() ->
					new Err<>(new CompileError("Node list '" + key + "' is empty", new NodeContext(node)));
			case Option.Some<?> _ -> innerRule.generate(node);
		};
	}
}
