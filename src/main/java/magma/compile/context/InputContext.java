package magma.compile.context;

import magma.compile.rule.TokenSequence;

public record InputContext(TokenSequence context) implements Context {
	@Override
	public String display(int depth) {
		return context.value();
	}
}
