package magma.app.locate;

import java.util.Optional;

public class LastLocator implements Locator {
    @Override
    public Optional<Integer> locate(String input, String infix) {
        final var index = input.lastIndexOf(infix);
        return index == -1 ? Optional.empty() : Optional.of(index);
    }
}