package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Result;

/**
 * A rule implementation that strips whitespace from the input before lexing.
 * <p>
 * This rule wraps another rule and removes leading and trailing whitespace
 * from the input string before passing it to the wrapped rule for lexing.
 * During generation, it simply delegates to the wrapped rule without modification.
 * <p>
 * This rule is useful for handling input that may contain extraneous whitespace
 * that should be ignored during parsing.
 */
public record StripRule(Rule rule) implements Rule {
	@Override
	public Result<String, CompileError> generate(final Node node) {
		return this.rule.generate(node);
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		return this.rule.lex(input.strip());
	}
}
