package magma.app.compile.rule;

import java.util.Optional;

public record First(String slice) implements Locator {
    @Override
    public Optional<Integer> locate(String input) {
        var index = input.indexOf(slice());
        return index == -1 ? Optional.empty() : Optional.of(index);
    }

    @Override
    public String createMessage() {
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