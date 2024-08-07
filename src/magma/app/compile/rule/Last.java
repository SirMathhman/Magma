package magma.app.compile.rule;

import java.util.Optional;

public record Last(String slice) implements Locator {
    @Override
    public Optional<Integer> locate(String input) {
        var index = input.lastIndexOf(slice());
        return index == -1 ? Optional.empty() : Optional.of(index);
    }

    @Override
    public String createErrorMessage() {
        return "Slice '" + slice + "' not present";
    }

    @Override
    public int length() {
        return slice.length();
    }

    @Override
    public String merge(String left, String right) {
        return left + slice + right;
    }
}