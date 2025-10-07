package magma.compile.rule;

import magma.list.Stream;

public interface Divider {
	Stream<TokenSequence> divide(TokenSequence tokenSequence);

	String delimiter();
}
