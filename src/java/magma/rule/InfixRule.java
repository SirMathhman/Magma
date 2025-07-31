package magma.rule;

import magma.node.Node;

import java.util.Optional;

public final class InfixRule implements Rule {
    private final Rule leftRule;
    private final Rule rightRule;
    private final String infix;

    public InfixRule(final Rule leftRule, final Rule rightRule, final String infix) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.infix = infix;
    }

    @Override
    public Optional<String> generate(final Node node) {
        final Optional<String> leftResult = this.leftRule.generate(node);
        final Optional<String> rightResult = this.rightRule.generate(node);
        
        if (leftResult.isPresent() && rightResult.isPresent()) {
            return Optional.of(leftResult.get() + this.infix + rightResult.get());
        } else if (leftResult.isPresent()) {
            return leftResult;
        } else if (rightResult.isPresent()) {
            return rightResult;
        } else {
            return Optional.empty();
        }
    }
}