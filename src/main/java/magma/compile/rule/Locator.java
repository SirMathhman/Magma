package magma.compile.rule;

import magma.option.Option;

public interface Locator {
	Option<Integer> locate(TokenSequence input, String infix);
}
