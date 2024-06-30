package magma.build.compile.rule;

import magma.api.result.Result;
import magma.build.compile.Error_;
import magma.build.compile.rule.result.RuleResult;

import java.util.List;

public final class OptionalRule implements Rule {
    private final String key;
    private final Rule onPresent;
    private final Rule onEmpty;
    private final OrRule orRule;

    public OptionalRule(String key, Rule onPresent, Rule onEmpty) {
        this.key = key;
        this.onPresent = onPresent;
        this.onEmpty = onEmpty;
        this.orRule = new OrRule(List.of(onPresent, onEmpty));
    }

    @Override
    public RuleResult toNode(String input) {
        return orRule.toNode(input);
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        if (node.has(key)) {
            return onPresent.fromNode(node);
        } else {
            return onEmpty.fromNode(node);
        }
    }
}
