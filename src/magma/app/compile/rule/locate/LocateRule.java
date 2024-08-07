package magma.app.compile.rule.locate;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.RuleResult;

import java.util.ArrayList;

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
        var occurrences = locator.locate(input).toList();
        var previous = new ArrayList<RuleResult<Node, ParseError>>();
        int i = 0;
        while (i < occurrences.size()) {
            var index = occurrences.get(i);
            var leftSlice = input.substring(0, index);
            var leftResult = leftRule.parse(leftSlice);

            var rightSlice = input.substring(index + locator.length());
            var result = leftResult.and(() -> rightRule.parse(rightSlice), Node::merge);
            if (result.isValid()) {
                return result;
            } else {
                previous.add(result);
            }
            i++;
        }

        return new RuleResult<>(Err.Err(new ParseError(locator.createErrorMessage(), input)), previous);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return leftRule.generate(node).and(() -> rightRule.generate(node), locator::merge);
    }
}