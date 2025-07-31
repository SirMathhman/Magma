package magma.rule;

import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
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
	public Result<String, String> generate(final Node node) {
		final Result<String, String> leftResult = this.leftRule.generate(node); if (leftResult.isErr()) {
			return leftResult;
		}

		final Result<String, String> rightResult = this.rightRule.generate(node); if (rightResult.isErr()) {
			return rightResult;
		}

		return new Ok<>(leftResult.unwrap() + this.infix + rightResult.unwrap());
	}

	@Override
	public Result<Node, String> lex(final String input) {
		final int infixIndex = input.indexOf(this.infix); if (-1 == infixIndex) {
			return new Err<>("Infix '" + this.infix + "' not found in input");
		}

		final String leftPart = input.substring(0, infixIndex);
		final String rightPart = input.substring(infixIndex + this.infix.length());

		final Result<Node, String> leftNode = this.leftRule.lex(leftPart); if (leftNode.isErr()) {
			return leftNode;
		}

		final Result<Node, String> rightNode = this.rightRule.lex(rightPart); if (rightNode.isErr()) {
			return rightNode;
		}

		return new Ok<>(leftNode.unwrap().merge(rightNode.unwrap()));
	}
}