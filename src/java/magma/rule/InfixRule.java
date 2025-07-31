package magma.rule;

import magma.node.Node;

import java.util.Optional;

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
	public Optional<String> generate(final Node node) {
		final Optional<String> leftResult = this.leftRule.generate(node);
		final Optional<String> rightResult = this.rightRule.generate(node);

		if (leftResult.isPresent() && rightResult.isPresent())
			return Optional.of(leftResult.get() + this.infix + rightResult.get());
		return Optional.empty();
	}

	@Override
	public Optional<Node> lex(final String input) {
		final int infixIndex = input.indexOf(this.infix);
		if (-1 == infixIndex) return Optional.empty();

		final String leftPart = input.substring(0, infixIndex);
		final String rightPart = input.substring(infixIndex + this.infix.length());

		final Optional<Node> leftNode = this.leftRule.lex(leftPart);
		final Optional<Node> rightNode = this.rightRule.lex(rightPart);

		if (leftNode.isPresent() && rightNode.isPresent()) return Optional.of(leftNode.get().merge(rightNode.get()));
		return Optional.empty();
	}
}