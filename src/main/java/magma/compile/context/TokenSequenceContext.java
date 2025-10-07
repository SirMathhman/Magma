package magma.compile.context;

import magma.compile.rule.TokenSequence;

public record TokenSequenceContext(TokenSequence context) implements Context {
	@Override
	public String display(int depth) {
		return context.display();
	}
}
