package magma.app.compile.rule;

import magma.api.error.list.ErrorSequence;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.string.Appending;

public final class InfixRule<Node, Error, NodeResult, StringResult extends Appending<StringResult>> implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> leftRule;
    private final String infix;
    private final Rule<Node, NodeResult, StringResult> rightRule;
    private final ResultFactory<Node, NodeResult, StringResult, ErrorSequence<Error>> factory;

    public InfixRule(final Rule<Node, NodeResult, StringResult> leftRule, final String infix, final Rule<Node, NodeResult, StringResult> rightRule, final ResultFactory<Node, NodeResult, StringResult, ErrorSequence<Error>> factory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(final String input) {
        final var separator = input.lastIndexOf(infix);
        if (0 > separator)
            return factory.fromNodeError("Infix '" + infix + "' not present", infix);

        final var rightSlice = input.substring(separator + infix.length());
        return rightRule.lex(rightSlice);
    }

    @Override
    public StringResult generate(final Node node) {
        return leftRule.generate(node)
                .appendSlice(infix)
                .tryAppendResult(() -> rightRule.generate(node));
    }
}