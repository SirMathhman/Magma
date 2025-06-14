package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.result.ResultRuleResults;
import magma.app.compile.rule.result.RuleResult;

public class EmptyRule implements Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> {
    @Override
    public RuleResult<CompoundNode> lex(String input) {
        return ResultRuleResults.createFromValue(new PropertiesCompoundNode());
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        return ResultRuleResults.createFromValue("");
    }
}
