package magma.compile.rule;

import java.util.Optional;

public record FirstLocator() implements Locator {
	@Override
	public Optional<Integer> locate(String input, String infix) {
		final int index = input.indexOf(infix);
		return index == -1 ? Optional.empty() : Optional.of(index);
	}
}
