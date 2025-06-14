package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.divide.FoldingDivider;
import magma.app.compile.rule.result.ResultRuleResults;
import magma.app.compile.rule.result.RuleResult;

import java.util.ArrayList;
import java.util.List;

public final class NodeListRule implements Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> {
    private final String key;
    private final Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> rule;

    public NodeListRule(String key, Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> rule) {
        this.key = key;
        this.rule = rule;
    }

    @Override
    public RuleResult<CompoundNode> lex(String input) {
        return new FoldingDivider().divide(input).stream().reduce(ResultRuleResults.createFromValue(new ArrayList<>()), NodeListRule.this::fold, (_, next) -> next).mapValue(children -> new PropertiesCompoundNode().nodeLists().with(this.key, children));
    }

    private RuleResult<List<CompoundNode>> fold(RuleResult<List<CompoundNode>> result, String s) {
        return result.flatMap(inner -> {
            return NodeListRule.this.rule.lex(s).mapValue(inner0 -> {
                inner.add(inner0);
                return inner;
            });
        });
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        final var compoundNodes = node.nodeLists().find(this.key).orElse(new ArrayList<>());
        return compoundNodes.stream().map(this.rule::generate).reduce(ResultRuleResults.createFromValue(""), (result, result1) -> result.flatMap(result0 -> result1.mapValue(result2 -> result0 + result2)), (_, next) -> next);
    }
}