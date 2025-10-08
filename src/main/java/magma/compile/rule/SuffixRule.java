package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.InputContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

public record SuffixRule(Rule rule, String suffix) implements Rule {
	public static Rule Suffix(Rule rule, String suffix) {
		return new SuffixRule(rule, suffix);
	}

	@Override
	public Result<Node, CompileError> lex(TokenSequence input) {
		if (!input.endsWith(suffix))
			return new Err<Node, CompileError>(new CompileError("Suffix '" + suffix + "' not present",
																													new InputContext(input)));
		final var slice = input.substring(0, input.length() - suffix.length());
		return rule.lex(slice);
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return rule.generate(node).mapValue(value -> value + suffix);
	}
}
