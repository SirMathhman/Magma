package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

/**
 * A rule implementation that matches and generates content with a specific suffix.
 * <p>
 * When lexing, this rule checks if the input ends with the specified suffix,
 * removes the suffix, and then applies the inner rule to the remaining content.
 * When generating, it applies the inner rule and then appends the suffix to the result.
 * <p>
 * This rule is useful for handling language constructs that have consistent
 * endings, such as semicolons at the end of statements or closing brackets.
 */
public final class SuffixRule implements Rule {
	private final Rule rule;
	private final String suffix;

	public SuffixRule(final Rule rule, final String suffix) {
		this.rule = rule; this.suffix = suffix;
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		final Result<String, CompileError> result = this.rule.generate(node); if (result.isErr()) return result;
		return result.mapValue(value -> value + this.suffix);
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		if (!input.endsWith(this.suffix)) {
			return new Err<>(CompileError.forLexing("Input does not end with suffix: " + this.suffix, input));
		}

		final String content = input.substring(0, input.length() - this.suffix.length()); return this.rule.lex(content);
	}
}