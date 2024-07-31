package magma;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SplitterTest {
    @Test
    void withinBraces() {
        var input = "{a;b}";
        var actual = Splitter.splitRootMembers(input);
        assertIterableEquals(Collections.singletonList(input), actual);
    }
}