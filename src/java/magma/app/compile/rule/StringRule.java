package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.result.RuleResult;

public final class StringRule implements Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> {
    private final String key;

    public StringRule(String key) {
        this.key = key;
    }

    @Override
    public RuleResult<CompoundNode> lex(String input) {
        return new RuleResult.RuleResultOk<>(new PropertiesCompoundNode().strings().with(this.key, input));
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        return node.strings().find(this.key).<RuleResult<String>>map(RuleResult.RuleResultOk::new).orElseGet(() -> RuleResult.createFromNode("String '" + this.key + "' not present", node));
    }
}