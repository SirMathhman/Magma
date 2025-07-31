package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Rule that contains a list of rules and tries each one in sequence until one succeeds.
 * For both lex and generate operations, it returns the first successful result.
 */
public final class OrRule implements Rule {
	private final List<Rule> rules;

	/**
	 * Creates a new FirstMatchRule with the specified list of rules.
	 *
	 * @param rules the rules to try in sequence
	 */
	public OrRule(final List<Rule> rules) {
		this.rules = List.copyOf(rules); // Create an immutable copy of the list
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		// Try each rule in sequence until one succeeds
		final Collection<CompileError> errors = new ArrayList<>();

		for (final Rule rule : this.rules) {
			final Result<String, CompileError> result = rule.generate(node); if (result.isOk()) return result;
			errors.add(result.unwrapErr());
		}

		// If no rule succeeds, return an error with all the child errors
		final CompileError error = new CompileError("All rules failed to generate");
		for (final CompileError childError : errors) {error.addChild(childError);} return new Err<>(error);
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		// Try each rule in sequence until one succeeds
		final List<CompileError> errors = new ArrayList<>();

		for (final Rule rule : this.rules) {
			final Result<Node, CompileError> result = rule.lex(input); if (result.isOk()) return result;
			errors.add(result.unwrapErr());
		}

		// If no rule succeeds, return an error with all the child errors
		final CompileError error = new CompileError("All rules failed to lex", new StringContext(input));
		for (final CompileError childError : errors) {error.addChild(childError);} return new Err<>(error);
	}
}