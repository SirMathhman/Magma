package magma.app.compile.lang.common;

import magma.api.Tuple;
import magma.app.compile.rule.locate.Locator;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class OperatorOpeningParenthesesLocator implements Locator {

    @Override
    public String createErrorMessage() {
        return "No opening parentheses present";
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public String merge(String left, String right) {
        return left + "(" + right;
    }

    @Override
    public Stream<Integer> locate(String input) {
        var depth = 0;

        var offset = input.length() - 1;
        var queue = IntStream.range(0, input.length())
                .map(index -> offset - index)
                .mapToObj(index -> new Tuple<>(index, input.charAt(index)))
                .collect(Collectors.toCollection(LinkedList::new));

        while (!queue.isEmpty()) {
            var tuple = queue.pop();
            var index = tuple.left();
            var c = tuple.right();

            if (c == '\"') {
                while (!queue.isEmpty()) {
                    var popped = queue.pop();

                    if (queue.isEmpty()) return Stream.empty();
                    var escaped = queue.peek().right();
                    if (escaped == '\\') {
                        queue.pop();
                    }

                    if (popped.right() == '\"') {
                        break;
                    }
                }
            }

            if (c == '(' && depth == 1) {
                return Stream.of(index);
            } else {
                if (c == ')') depth++;
                if (c == '(') depth--;
            }
        }

        return Stream.empty();
    }
}
