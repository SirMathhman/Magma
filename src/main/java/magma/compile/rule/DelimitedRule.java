package magma.compile.rule;

import magma.list.Stream;

import java.util.regex.Pattern;

public record DelimitedRule(String delimiter) implements Divider {
	@Override
	public Stream<TokenSequence> divide(TokenSequence tokenSequence) {
		return tokenSequence.split(Pattern.quote(delimiter)).stream();
	}
}
