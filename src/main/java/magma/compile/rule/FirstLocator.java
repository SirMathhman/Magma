package magma.compile.rule;

import magma.option.Option;

public class FirstLocator implements Locator {
	@Override
	public Option<Integer> locate(Slice input, String infix) {
		return input.indexOf(infix);
	}
}
