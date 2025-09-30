package magma.compile.rule;

import magma.option.Option;

public record FirstLocator() implements Locator {
	@Override
	public Option<Integer> locate(String input, String infix) {
		final int index = input.indexOf(infix); return index == -1 ? Option.empty() : Option.of(index);
	}
}
