package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

/**
 * A rule implementation that matches and generates content with a specific prefix.
 * <p>
 * When lexing, this rule checks if the input starts with the specified prefix,
 * removes the prefix, and then applies the inner rule to the remaining content.
 * When generating, it applies the inner rule and then prepends the prefix to the result.
 * <p>
 * This rule is useful for handling language constructs that have consistent
 * beginnings, such as keywords at the start of statements or opening brackets.
 */
public final class PrefixRule implements Rule {
	private final Rule rule;
	private final String prefix;

	public PrefixRule(final String prefix, final Rule rule) {
		this.rule = rule; this.prefix = prefix;
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		final Result<String, CompileError> result = this.rule.generate(node); if (result.isErr()) return result;
		return result.mapValue(value -> this.prefix + value);
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		if (!input.startsWith(this.prefix)) {
			return new Err<>(CompileError.forLexing("Input does not start with prefix: " + this.prefix, input));
		}

		final String content = input.substring(this.prefix.length()); return this.rule.lex(content);
	}
}