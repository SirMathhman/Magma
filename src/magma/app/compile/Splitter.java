package magma.app.compile;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Splitter(String input) {
    public static final char STATEMENT_END = ';';
    public static final char BLOCK_START = '{';
    public static final char BLOCK_END = '}';

    static State splitAtChar(State state, char c) {
        var appended = state.append(c);
        if (c == STATEMENT_END && appended.isLevel()) return appended.advance();
        if (c == BLOCK_START) return appended.enter();
        if (c == BLOCK_END) return appended.exit();
        return appended;
    }

    Stream<String> split() {
        return IntStream.range(0, input().length())
                .mapToObj(input()::charAt)
                .reduce(new State(), Splitter::splitAtChar, (previous, next) -> next)
                .advance()
                .stream();
    }
}