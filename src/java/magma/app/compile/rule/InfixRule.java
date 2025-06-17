package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.MergingNode;

import java.util.Objects;

public final class InfixRule<Node extends MergingNode<Node>, Error> implements Rule<Node, Result<Node, Error>, Result<String, Error>> {
    private final Rule<Node, Result<Node, Error>, Result<String, Error>> leftRule;
    private final String infix;
    private final Rule<Node, Result<Node, Error>, Result<String, Error>> rightRule;
    private final ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory;

    public InfixRule(Rule<Node, Result<Node, Error>, Result<String, Error>> leftRule, String infix, Rule<Node, Result<Node, Error>, Result<String, Error>> rightRule, ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.factory = factory;
    }

    @Override
    public Result<Node, Error> lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return this.factory.fromStringErr("Infix '" + this.infix + "' not present", input);

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        return this.leftRule.lex(left)
                .flatMapValue(leftResult -> this.rightRule.lex(right)
                        .mapValue(leftResult::merge));
    }

    @Override
    public Result<String, Error> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMapValue(leftResult -> this.rightRule.generate(node)
                        .mapValue(rightResult -> leftResult + this.infix + rightResult));
    }

    public Rule<Node, Result<Node, Error>, Result<String, Error>> leftRule() {
        return this.leftRule;
    }

    public String infix() {
        return this.infix;
    }

    public Rule<Node, Result<Node, Error>, Result<String, Error>> rightRule() {
        return this.rightRule;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (InfixRule) obj;
        return Objects.equals(this.leftRule, that.leftRule) && Objects.equals(this.infix, that.infix) && Objects.equals(
                this.rightRule,
                that.rightRule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.leftRule, this.infix, this.rightRule);
    }

    @Override
    public String toString() {
        return "InfixRule[" + "leftRule=" + this.leftRule + ", " + "infix=" + this.infix + ", " + "rightRule=" + this.rightRule + ']';
    }

}
