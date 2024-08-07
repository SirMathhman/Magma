package magma.app.compile.rule.locate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BackwardsLocator implements Locator {
    private final String slice;

    public BackwardsLocator(String slice) {
        this.slice = slice;
    }

    @Override
    public Stream<Integer> locate(String input) {
        return IntStream.range(0, input.length() - slice.length() + 1)
                .map(i -> input.length() - slice.length() - i) // Generate indices in reverse order
                .filter(i -> input.substring(i, i + slice.length()).equals(slice))
                .boxed();
    }

    @Override
    public String createErrorMessage() {
        return "No instances of '" + slice.length() + "' present";
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
