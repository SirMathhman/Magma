package magma.app.compile.rule.locate;

import java.util.stream.Stream;

public interface Locator {
    Stream<Integer> locate(String input);

    String createErrorMessage();

    int length();

    String merge(String left, String right);
}
