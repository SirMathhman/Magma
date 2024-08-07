package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public final class LocateRule implements Rule {
    private final Rule leftRule;
    private final Rule rightRule;
    private final Locator locator;

    public LocateRule(Rule leftRule, Locator locator, Rule rightRule) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.locator = locator;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        var startIndex = locator.locate(input);
        if (startIndex.isEmpty())
            return new RuleResult<>(Err.Err(new ParseError(locator.createErrorMessage(), input)));

        var leftSlice = input.substring(0, startIndex.get());
        var leftResult = leftRule.parse(leftSlice);

        var rightSlice = input.substring(startIndex.get() + locator.length());
        return leftResult.and(() -> rightRule.parse(rightSlice), Node::merge);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return leftRule.generate(node).and(() -> rightRule.generate(node), (left, right) -> locator.merge(left, right));
    }
}