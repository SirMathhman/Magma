package magma.rule;

import magma.MapNode;

import java.util.Optional;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
	@Override
	public Optional<String> generate(final MapNode node) {
		return this.leftRule().generate(node).flatMap(leftResult -> {
			return this.rightRule().generate(node).map(rightResult -> leftResult + this.infix() + rightResult);
		});
	}
}