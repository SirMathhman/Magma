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

        return new TypeRule(type, new LocateRule(caller, new OperatorOpeningParenthesesLocator(), new StripRule(new SuffixRule(arguments, ")"))));
    }

    public static TypeRule createInvocationStatementRule(Rule value) {
        return new TypeRule(INVOCATION, new SuffixRule(createInvocationRule(value), ";"));
    }
}
