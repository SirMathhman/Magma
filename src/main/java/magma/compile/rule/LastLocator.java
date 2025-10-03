package magma.compile.rule;

import magma.option.Option;

public record LastLocator() implements Locator {
	@Override
	public Option<Integer> locate(String input, String infix) {
		final int index = input.lastIndexOf(infix);
		if (index == -1) return Option.empty();
		return Option.of(index);
	}
}
