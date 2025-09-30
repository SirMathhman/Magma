package magma.compile.rule;

import magma.option.Option;

public record LastLocator() implements Locator {
	@Override
	public Option<Integer> locate(String input, String infix) {
		final int index = input.lastIndexOf(infix); return index == -1 ? Option.empty() : Option.of(index);
	}
}
