package magma.app.compile;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SplitterTest {
    @Test
    void braces() {
        var expected = "{{a;b}}";
        var actual = new Splitter(expected)
                .split()
                .collect(Collectors.toSet());
        assertIterableEquals(Collections.singleton(expected), actual);
    }
}