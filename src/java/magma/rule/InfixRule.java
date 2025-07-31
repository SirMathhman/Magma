package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

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
		final Result<String, CompileError> leftResult = this.leftRule.generate(node); if (leftResult.isErr())
			return leftResult;

		final Result<String, CompileError> rightResult = this.rightRule.generate(node); if (rightResult.isErr())
			return rightResult;

		return leftResult.flatMapValue(
				leftValue -> rightResult.mapValue(rightValue -> leftValue + this.infix + rightValue));
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		final int infixIndex = input.indexOf(this.infix); if (-1 == infixIndex)
			return new Err<>(new CompileError("Infix '" + this.infix + "' not found in input", new StringContext(input)));

		final String leftPart = input.substring(0, infixIndex);
		final String rightPart = input.substring(infixIndex + this.infix.length());

		final Result<Node, CompileError> leftNode = this.leftRule.lex(leftPart); if (leftNode.isErr()) return leftNode;

		final Result<Node, CompileError> rightNode = this.rightRule.lex(rightPart); if (rightNode.isErr()) return rightNode;

		return leftNode.flatMapValue(left -> rightNode.mapValue(right -> left.merge(right)));
	}
}