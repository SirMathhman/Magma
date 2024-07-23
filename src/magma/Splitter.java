package magma;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Splitter {
    static List<String> split(String input) {
        var queue = IntStream.range(0, input.length())
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new));
        var current = new State(queue);

        while (true) {
            var maybeNext = splitAtChar(current);
            if (maybeNext.isEmpty()) break;
            current = maybeNext.get();
        }

        return current.advance().segments;
    }

    static Optional<State> splitAtChar(State state) {
        return state.appendAndRetrieve().map(Splitter::splitAtCharPopped);
    }

    static State splitAtCharPopped(Tuple<State, Character> tuple) {
        var appended = tuple.left();
        var c = tuple.right();
        if (c == '\\') return splitAtSingleQuotes(appended);
        if (c == '\"') return splitAtDoubleQuotes(appended);
        if (c == ';' && appended.isLevel()) return appended.advance();
        if (c == '}' && appended.isShallow()) return appended.advance();
        if (c == '{') return appended.enter();
        if (c == '}') return appended.exit();
        return appended;
    }

    static State splitAtSingleQuotes(State state) {
        return state.appendAndRetrieve()
                .flatMap(tuple -> tuple.right() == '\\' ? tuple.left().append() : Optional.of(tuple.left()))
                .flatMap(State::append)
                .orElse(state);
    }

    static State splitAtDoubleQuotes(State appended) {
        var current = appended;
        while (true) {
            var nextTuple = current.appendAndRetrieve();
            if (nextTuple.isEmpty()) return current;
            var nextState = nextTuple.get().left();
            var next = nextTuple.get().right();

            if (next == '\"') return nextState;
            if (next == '\\') {
                current = nextState.append().orElse(nextState);
            } else {
                current = nextState;
            }
        }
    }

}