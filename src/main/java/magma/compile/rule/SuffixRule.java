package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

public record SuffixRule(Rule rule, String suffix) implements Rule {
	public static Rule Suffix(Rule rule, String suffix) {
		return new SuffixRule(rule, suffix);
	}

	@Override
	public Result<Node, CompileError> lex(String input) {
		if (!input.endsWith(suffix()))
			return new Err<Node, CompileError>(new CompileError("Suffix '" + suffix + "' not present", new StringContext(input)));
		final String slice = input.substring(0, input.length() - suffix().length());
		return getRule().lex(slice);
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return rule.generate(node).mapValue(value -> value + suffix());
	}

	public Rule getRule() {
		return rule;
	}
}
