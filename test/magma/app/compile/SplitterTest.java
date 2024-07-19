package magma.app.compile;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SplitterTest {
    @Test
    void semicolonWithinBraces() {
        var value = "{a;b}";
        var expected = Collections.singletonList(value);
        var actual = Splitter.split(value).toList();
        assertIterableEquals(expected, actual);
    }
}