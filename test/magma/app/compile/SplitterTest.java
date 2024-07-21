package magma.app.compile;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SplitterTest {
    @Test
    void splitWithBraces() {
        var input = "{a;b}";
        var expected = Collections.singletonList(input);
        var actual = Splitter.split(input);
        assertIterableEquals(expected, actual);
    }
}