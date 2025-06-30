package magma.rule.locate;

import java.util.Optional;

public class FirstLocator implements Locator {
    @Override
    public Optional<Integer> locate(final String input, final String infix) {
        final var index = input.indexOf(infix);
        if (-1 == index) return Optional.empty();
        return Optional.of(index);
    }
}
