package magma.app.compile.lang.common;

import magma.app.compile.split.ParamSplitter;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OptionalRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.LocateRule;
import magma.app.compile.rule.locate.Locator;

import java.util.Optional;
import java.util.stream.Stream;

public class Operations {
    public static final String INVOCATION = "invocation";

    public static TypeRule createInvocationRule(Rule value) {
        return createOperationsRule(INVOCATION, new NodeRule("caller", value), value);
    }

    public static TypeRule createOperationsRule(String type, Rule caller, Rule value) {
        var arguments = new OptionalRule("arguments",
                new NodeListRule(new ParamSplitter(), "arguments", value),
                EmptyRule.EMPTY_RULE
        );

        return new TypeRule(type, new LocateRule(caller, new InvocationLocator(), new StripRule(new SuffixRule(arguments, ")"))));
    }

    public static TypeRule createInvocationStatementRule(Rule value) {
        return new TypeRule(INVOCATION, new SuffixRule(createInvocationRule(value), ";"));
    }

    private static class InvocationLocator implements Locator {
        private Optional<Integer> locate0(String input) {
            var depth = 0;
            int i = input.length() - 1;
            while (i >= 0) {
                var c = input.charAt(i);
                if (c == '(' && depth == 1) {
                    return Optional.of(i);
                } else {
                    if (c == ')') depth++;
                    if (c == '(') depth--;
                }
                i--;
            }

            return Optional.empty();
        }

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
            return locate0(input).stream();
        }
    }
}
