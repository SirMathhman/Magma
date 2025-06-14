package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.optional.ResultRuleResult;

public final class StringRule implements Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> {
    private final String key;

    public StringRule(String key) {
        this.key = key;
    }

    @Override
    public RuleResult<CompoundNode> lex(String input) {
        return ResultRuleResult.createFromValue(new PropertiesCompoundNode().strings().with(this.key, input));
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        return node.strings().find(this.key).map(ResultRuleResult::createFromValue).orElseGet(() -> ResultRuleResult.createFromNode("String '" + this.key + "' not present", node));
    }
}