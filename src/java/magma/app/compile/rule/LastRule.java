package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.CompileResultFactory;

public final class LastRule<Node> implements Rule<Node> {
    private final Rule<Node> leftRule;
    private final String infix;
    private final Rule<Node> rightRule;
    private final CompileResultFactory<Node> resultFactory;

    public LastRule(Rule<Node> leftRule, String infix, Rule<Node> rightRule, CompileResultFactory<Node> resultFactory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.resultFactory = resultFactory;
    }

    @Override
    public CompileResult<Node> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return this.resultFactory.fromStringError("Infix '" + this.infix + "' not present", input);

        final var rightResult = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightResult);
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> this.rightRule.generate(node)
                        .mapValue(rightResult -> leftResult + this.infix + rightResult));
    }
}