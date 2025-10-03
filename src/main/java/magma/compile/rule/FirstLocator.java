package magma.compile.rule;

import magma.option.Option;

public record FirstLocator() implements Locator {
	@Override
	public Option<Integer> locate(String input, String infix) {
		final int index = input.indexOf(infix);
		if (index == -1) return Option.empty();
		return Option.of(index);
	}
}
