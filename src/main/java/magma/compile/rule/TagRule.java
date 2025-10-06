package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

import java.util.List;

public record TagRule(String tag, Rule rule) implements Rule {
	public static Rule Tag(String type, Rule rule) {
		return new TagRule(type, rule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content)
							 .mapValue(node -> node.retype(tag))
							 .mapErr(error -> new CompileError("Failed to attach tag '" + tag + "'",
																								 new StringContext(content),
																								 List.of(error)));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		if (node.is(tag)) return rule.generate(node)
																 .mapErr(error -> new CompileError("Failed to generate with tag '" + tag + "'",
																																	 new NodeContext(node),
																																	 List.of(error)));

		else return new Err<>(new CompileError("Type '" + tag + "' not present", new NodeContext(node)));
	}
}
