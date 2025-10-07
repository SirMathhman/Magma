package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.list.NonEmptyList;
import magma.option.None;
import magma.option.Some;
import magma.result.Err;
import magma.result.Result;

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
			case None<?> _ ->
				new Err<String, CompileError>(new CompileError("Node list '" + key + "' not present", new NodeContext(node)));
			// NonEmptyList is never empty, so if it's present, we can use it
			case Some(NonEmptyList<Node> _) -> innerRule.generate(node);
		};
	}
}
