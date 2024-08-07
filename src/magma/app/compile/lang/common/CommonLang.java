package magma.app.compile.lang.common;

import magma.api.Tuple;
import magma.app.compile.ParamSplitter;
import magma.app.compile.lang.DigitRule;
import magma.app.compile.lang.SymbolRule;
import magma.app.compile.lang.java.JavaLang;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.locate.LocateRule;
import magma.app.compile.rule.locate.Locator;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OptionalRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringListRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CommonLang {
    public static final String MODIFIERS = "modifiers";
    public static final String PARAMS = "params";
    public static final String INVOCATION = "invocation";

    public static Rule createImportRule() {
        return createNamespaceRule("import", "import ");
    }

    public static Rule createNamespaceRule(String type, String prefix) {
        return new TypeRule(type, new PrefixRule(prefix, new SuffixRule(new StringListRule("namespace", "."), ";")));
    }

    public static StripRule createModifiersRule() {
        return new StripRule(new StringListRule(MODIFIERS, " "));
    }

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

    public static TypeRule createReferenceRule() {
        return new TypeRule("reference", new StripRule(new SymbolRule(new StringRule("value"))));
    }

    public static TypeRule createInvocationStatementRule(Rule value) {
        return new TypeRule(INVOCATION, new SuffixRule(createInvocationRule(value), ";"));
    }

    public static TypeRule createAccessRule(LazyRule value) {
        return new TypeRule("access", new LocateRule(new NodeRule("object", value), new Last("."), new StringRule("member")));
    }

    public static TypeRule createCatchRule(Rule definition, LazyRule statement) {
        return PrefixedStatements.createPrefixedStatementRule("catch", "catch", statement, children -> captureCatchParameters(children, definition));
    }

    private static Rule captureCatchParameters(Rule children, Rule definition) {
        var params = new NodeListRule(new ParamSplitter(), "params", definition);
        return new StripRule(new PrefixRule("(", new LocateRule(params, new First(")"), children)));
    }

    public static TypeRule createCommentRule() {
        return new TypeRule("comment", new PrefixRule("//", new StringRule("value")));
    }

    public static TypeRule createReturnRule(Rule value) {
        return new TypeRule("return", new PrefixRule("return ", new SuffixRule(new NodeRule("value", value), ";")));
    }

    public static NodeListRule createParamsRule(Rule definition) {
        return new NodeListRule(new ParamSplitter(), PARAMS, definition);
    }

    public static TypeRule createStringRule() {
        return new TypeRule("string", new StripRule(new PrefixRule("\"", new SuffixRule(new StringRule("value"), "\""))));
    }

    public static TypeRule createDefinitionStatement(Rule definition) {
        return new TypeRule("definition", new SuffixRule(definition, ";"));
    }

    public static TypeRule createAssignmentRule(Rule value) {
        var assignable = new LazyRule();
        assignable.set(new DisjunctionRule(List.of(
                new StringRule("value")
        )));

        var valueProperty = new NodeRule("value", value);
        return new TypeRule("assignment", new LocateRule(assignable, new First("="), new SuffixRule(valueProperty, ";")));
    }

    public static TypeRule createConditionRule(String type, String prefix, Rule value, Rule statement) {
        var valueProperty = new NodeRule("value", new DisjunctionRule(List.of(
                new StripRule(new PrefixRule("{", new SuffixRule(Blocks.createBlockRule(statement), "}"))),
                statement
        )));

        var condition = new NodeRule("condition", value);
        var child = new LocateRule(condition, new ClosingParenthesesLocator(), valueProperty);

        return new TypeRule(type, new PrefixRule(prefix, new StripRule(new PrefixRule("(", child))));
    }

    public static TypeRule createNumberRule() {
        return new TypeRule("number", new StripRule(new DigitRule(new StringRule("value"))));
    }

    public static TypeRule createOperatorRule(String type, String operator, LazyRule value) {
        return new TypeRule(type, new LocateRule(new NodeRule("left", value), new First(operator), new NodeRule("right", value)));
    }

    public static TypeRule createCharRule() {
        return new TypeRule("char", new StripRule(new PrefixRule("'", new SuffixRule(new StringRule("value"), "'"))));
    }

    public static TypeRule createElseRule(LazyRule statement) {
        var value = new NodeRule("value", Blocks.createBlockRule(statement));
        return new TypeRule("else", new PrefixRule("else", new StripRule(new PrefixRule("{", new SuffixRule(value, "}")))));
    }

    public static TypeRule createPostDecrementRule(Rule value) {
        return new TypeRule("post-decrement", new StripRule(new SuffixRule(new NodeRule("child", value), "--;")));
    }

    public static Rule createStructRule(String type, String slice, Rule member) {
        var classRule = new LazyRule();
        var modifiers = createModifiersRule();

        var content = new NodeRule("value", Blocks.createBlockRule(member));
        var after = new LocateRule(new StripRule(new StringRule(JavaLang.CLASS_NAME)), new First("{"), new SuffixRule(content, "}"));
        classRule.set(new TypeRule(type, new LocateRule(modifiers, new First(slice), after)));
        return classRule;
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
