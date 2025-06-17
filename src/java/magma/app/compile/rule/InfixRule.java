package magma.app.compile.rule;

import magma.app.compile.AppendableStringResult;
import magma.app.compile.FormattedError;
import magma.app.compile.MergeNodeResult;
import magma.app.compile.ResultFactory;

public final class InfixRule<Node, NodeResult extends MergeNodeResult<Node, NodeResult>, StringResult extends AppendableStringResult<StringResult>> implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> leftRule;
    private final String infix;
    private final Rule<Node, NodeResult, StringResult> rightRule;
    private final ResultFactory<Node, FormattedError, NodeResult, StringResult> factory;

    public InfixRule(Rule<Node, NodeResult, StringResult> leftRule, String infix, Rule<Node, NodeResult, StringResult> rightRule, ResultFactory<Node, FormattedError, NodeResult, StringResult> factory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return this.factory.fromStringErr("Infix '" + this.infix + "' not present", input);

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        return this.leftRule.lex(left)
                .mergeResult(() -> this.rightRule.lex(right));
    }

    @Override
    public StringResult generate(Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .appendResult(() -> this.rightRule.generate(node));
    }
}
