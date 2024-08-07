package magma.app.compile.lang.common;

import magma.app.compile.ParamSplitter;
import magma.app.compile.lang.SymbolRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.First;
import magma.app.compile.rule.Last;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.Locator;
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

import java.util.Optional;

public class CommonLang {
    public static final String MODIFIERS = "modifiers";
    public static final String PARAMS = "params";

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
        return createOperationsRule("invocation", value, new NodeRule("caller", value));
    }

    public static TypeRule createOperationsRule(String type, Rule value, Rule caller) {
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
        return new TypeRule("invocation", new SuffixRule(createInvocationRule(value), ";"));
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

    private static class InvocationLocator implements Locator {
        @Override
        public Optional<Integer> locate(String input) {
            var depth = 0;
            for (int i = input.length() - 1; i >= 0; i--) {
                var c = input.charAt(i);
                if (c == '(' && depth == 1) {
                    return Optional.of(i);
                } else {
                    if (c == ')') depth++;
                    if (c == '(') depth--;
                }
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
    }
}
