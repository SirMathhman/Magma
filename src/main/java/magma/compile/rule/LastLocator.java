package magma.compile.rule;

import magma.option.Option;

public record LastLocator() implements Locator {
	@Override
	public Option<Integer> locate(Slice input, String infix) {
		return input.lastIndexOf(infix);
	}
}
