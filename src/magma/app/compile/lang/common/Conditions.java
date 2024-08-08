package magma.app.compile.lang.common;

import magma.api.Tuple;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.LocateRule;
import magma.app.compile.rule.locate.Locator;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Conditions {
    public static TypeRule createConditionRule(String type, String prefix, Rule value, Rule statement) {
        var valueProperty = new NodeRule("value", new DisjunctionRule(List.of(
                new StripRule(new PrefixRule("{", new SuffixRule(Blocks.createBlockRule(statement), "}"))),
                statement
        )));

        var condition = new NodeRule("condition", value);
        var child = new LocateRule(condition, new ClosingParenthesesLocator(), valueProperty);

        return new TypeRule(type, new PrefixRule(prefix, new StripRule(new PrefixRule("(", child))));
    }

    public static TypeRule createElseRule(LazyRule statement) {
        var value = new NodeRule("value", Blocks.createBlockRule(statement));
        return new TypeRule("else", new PrefixRule("else", new StripRule(new PrefixRule("{", new SuffixRule(value, "}")))));
    }

    public static TypeRule createBreakRule() {
        return new TypeRule("break", new StripRule(new SuffixRule(EmptyRule.EMPTY_RULE, "break;")));
    }

    public static TypeRule createContinueRule() {
        return new TypeRule("continue", new StripRule(new SuffixRule(EmptyRule.EMPTY_RULE, "continue;")));
    }

    public static TypeRule createTernaryRule(LazyRule value) {
        var condition = new NodeRule("condition", value);
        var whenTrue = new NodeRule("whenTrue", value);
        var whenFalse = new NodeRule("whenFalse", value);
        return new TypeRule("ternary", new LocateRule(condition, new First("?"),
                new LocateRule(whenTrue, new First(":"), whenFalse)));
    }

    private static class ClosingParenthesesLocator implements Locator {
        private Optional<Integer> locate0(String input) {
            var depth = 0;
            var queue = IntStream.range(0, input.length())
                    .mapToObj(index -> new Tuple<>(index, input.charAt(index)))
                    .collect(Collectors.toCollection(LinkedList::new));

            while (!queue.isEmpty()) {
                var popped = queue.pop();
                var i = popped.left();
                var c = popped.right();

                if (c == '\'') {
                    var escaped = queue.pop();
                    if (escaped.right() == '\\') {
                        queue.pop();
                    }

                    queue.pop();
                    continue;
                }

                if (c == ')' && depth == 0) {
                    return Optional.of(i);
                } else {
                    if (c == '(') depth++;
                    if (c == ')') depth--;
                }
            }

            return Optional.empty();
        }

        @Override
        public String createErrorMessage() {
            return "No closing parentheses present";
        }

        @Override
        public int length() {
            return 1;
        }

        @Override
        public String merge(String left, String right) {
            return left + ")" + right;
        }

        @Override
        public Stream<Integer> locate(String input) {
            return locate0(input).stream();
        }
    }
}
