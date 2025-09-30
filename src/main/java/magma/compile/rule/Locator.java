package magma.compile.rule;

import magma.option.Optional;

public interface Locator {
	Optional<Integer> locate(String input, String infix);
}
