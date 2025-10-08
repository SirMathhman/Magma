package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.TokenSequenceContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

public record PrefixRule(String prefix, Rule rule) implements Rule {
	public static Rule Prefix(String prefix, Rule rule) {
		return new PrefixRule(prefix, rule);
	}

	@Override
	public Result<Node, CompileError> lex(TokenSequence content) {
		if (content.startsWith(prefix)) return rule.lex(content.substring(prefix.length()));
		else return new Err<Node, CompileError>(new CompileError("Prefix '" + prefix + "' not present", new TokenSequenceContext(content)));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return rule.generate(node).mapValue(inner -> prefix + inner);
	}
}
