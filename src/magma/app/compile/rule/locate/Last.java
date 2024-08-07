package magma.app.compile.rule.locate;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class Last implements Locator {
    private final String slice;

    public Last(String slice) {
        this.slice = slice;
    }

    private Optional<Integer> locate0(String input) {
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

    @Override
    public Stream<Integer> locate(String input) {
        return locate0(input).stream();
    }

    public String slice() {
        return slice;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Last) obj;
        return Objects.equals(this.slice, that.slice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slice);
    }

    @Override
    public String toString() {
        return "Last[" +
               "slice=" + slice + ']';
    }

}