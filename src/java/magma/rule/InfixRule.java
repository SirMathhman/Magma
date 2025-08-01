package magma.rule;

import magma.error.CompileError;
import magma.input.Input;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

/**
 * A rule implementation that handles content with an infix separator.
 * <p>
 * When lexing, this rule looks for the specified infix string in the input,
 * splits the input at that point, and applies the left and right rules to
 * the respective parts. The resulting nodes are then merged.
 * <p>
 * When generating, it applies both the left and right rules and combines
 * their results with the infix string in between.
 * <p>
 * This rule is useful for handling language constructs with infix operators
 * or separators, such as binary expressions or declarations with initializers.
 */
public final class InfixRule implements Rule {
	private final Rule leftRule;
	private final Rule rightRule;
	private final String infix;

	public InfixRule(final Rule leftRule, final String infix, final Rule rightRule) {
		this.leftRule = leftRule;
		this.rightRule = rightRule;
		this.infix = infix;
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		final Result<String, CompileError> leftResult = this.leftRule.generate(node);
		if (leftResult.isErr()) return leftResult;

		final Result<String, CompileError> rightResult = this.rightRule.generate(node);
		if (rightResult.isErr()) return rightResult;

		return leftResult.flatMapValue(
				leftValue -> rightResult.mapValue(rightValue -> leftValue + this.infix + rightValue));
	}

	@Override
	public Result<Node, CompileError> lex(final Input input) {
		final int infixIndex = input.indexOf(this.infix);
		if (-1 == infixIndex) {
			return new Err<>(CompileError.forLexing("Infix '" + this.infix + "' not found in input", input.getContent()));
		}

		try {
			final Input[] parts = input.splitAtInfix(this.infix); final Input leftInput = parts[0];
			final Input rightInput = parts[1];

			final Result<Node, CompileError> leftNode = this.leftRule.lex(leftInput); if (leftNode.isErr()) return leftNode;

			final Result<Node, CompileError> rightNode = this.rightRule.lex(rightInput);
			if (rightNode.isErr()) return rightNode;

			return leftNode.flatMapValue(left -> rightNode.mapValue(left::merge));
		} catch (IllegalArgumentException e) {
			// This should never happen since we already checked indexOf
			return new Err<>(CompileError.forLexing("Error processing infix: " + e.getMessage(), input.getContent()));
		}
	}
}