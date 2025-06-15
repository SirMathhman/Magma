package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.divide.FoldingDivider;
import magma.app.compile.rule.result.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class NodeListRule implements Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> {
    private final String key;
    private final Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> rule;

    public NodeListRule(String key, Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> rule) {
        this.key = key;
        this.rule = rule;
    }

    @Override
    public RuleResult<CompoundNode> lex(String input) {
        RuleResult<List<CompoundNode>> listRuleResult = new FoldingDivider().divide(input).stream().reduce(RuleResult.createFromValue(new ArrayList<>()), NodeListRule.this::fold, (_, next) -> next);
        return listRuleResult.<RuleResult<CompoundNode>>match(value -> new RuleResult.Ok<>(((Function<List<CompoundNode>, CompoundNode>) children -> new PropertiesCompoundNode().nodeLists().with(this.key, children)).apply(value)), RuleResult.Err::new);
    }

    private RuleResult<List<CompoundNode>> fold(RuleResult<List<CompoundNode>> result, String s) {
        return result.match(inner -> {
            RuleResult<CompoundNode> compoundNodeRuleResult = this.rule.lex(s);
            return compoundNodeRuleResult.<RuleResult<List<CompoundNode>>>match(value -> new RuleResult.Ok<>(((Function<CompoundNode, List<CompoundNode>>) inner0 -> {
                inner.add(inner0);
                return inner;
            }).apply(value)), RuleResult.Err::new);
        }, RuleResult.Err::new);
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        final var compoundNodes = node.nodeLists().find(this.key).orElse(new ArrayList<>());
        return compoundNodes.stream().map(this.rule::generate).reduce(RuleResult.createFromValue(""), (result, result1) -> result.match(result0 -> result1.<RuleResult<String>>match(value -> new RuleResult.Ok<>(((Function<String, String>) result2 -> result0 + result2).apply(value)), RuleResult.Err::new), RuleResult.Err::new), (_, next) -> next);
    }
}