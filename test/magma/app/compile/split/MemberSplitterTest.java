package magma.app.compile.split;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class MemberSplitterTest {
    private static void assertSplit(String input, List<String> output) {
        var actual = new MemberSplitter().split(input);
        assertIterableEquals(output, actual);
    }

    @Test
    void doubleQuotesEscape() {
        assertSplit("\"\\\"\"", List.of("\"\\\"\""));
    }

    @Test
    void doubleQuotes() {
        assertSplit("\"foo;bar\"", List.of("\"foo;bar\""));
    }

    @Test
    void singleQuotesEscape() {
        assertSplit("'\\';'a'", List.of("'\\';", "'a'"));
    }

    @Test
    void singleQuotes() {
        assertSplit("';'", List.of("';'"));
    }

    @Test
    void comment() {
        assertSplit("//foo;\nbar", List.of("//foo;\nbar"));
    }

    @Test
    void twoBraces() {
        assertSplit("{a}{b}", List.of("{a}", "{b}"));
    }

    @Test
    void simple() {
        assertSplit("a;b", List.of("a;", "b"));
    }

    @Test
    void oneBrace() {
        var input = "{a;b}";
        assertSplit(input, Collections.singletonList(input));
    }
}