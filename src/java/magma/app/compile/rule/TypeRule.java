package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.result.ResultRuleResults;
import magma.app.compile.rule.result.RuleResult;

public record TypeRule(String type,
                       Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> rule) implements Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> {
    @Override
    public RuleResult<CompoundNode> lex(String input) {
        return this.rule.lex(input).mapValue(node -> node.retype(this.type));
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        else
            return ResultRuleResults.createFromNode("Type '" + this.type + "' not present", node);
    }
}
