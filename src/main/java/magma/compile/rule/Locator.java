package magma.compile.rule;

import magma.option.Option;

public interface Locator {
	Option<Integer> locate(String input, String infix);
}
