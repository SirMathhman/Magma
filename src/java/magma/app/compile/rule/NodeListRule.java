package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.divide.FoldingDivider;
import magma.app.compile.rule.result.ResultRuleResults;
import magma.app.compile.rule.result.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return ResultRuleResults.createFromValue(node.nodeLists().find(this.key).orElse(new ArrayList<>()).stream().map(source -> this.rule.generate(source).findAsOption()).flatMap(Optional::stream).collect(Collectors.joining()));
    }
}