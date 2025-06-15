package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.divide.FoldingDivider;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.RuleResult.RuleResultErr;
import magma.app.compile.rule.result.RuleResult.RuleResultOk;

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
        return switch (listRuleResult) {
            case RuleResultErr<List<CompoundNode>>(var error) -> new RuleResultErr<>(error);
            case RuleResultOk<List<CompoundNode>>(
                    List<CompoundNode> value1
            ) -> new RuleResultOk<>(new PropertiesCompoundNode().nodeLists().with(this.key, value1));
        };
    }

    private RuleResult<List<CompoundNode>> fold(RuleResult<List<CompoundNode>> result, String s) {
        return switch (result) {
            case RuleResultErr<List<CompoundNode>>(var error1) -> new RuleResultErr<>(error1);
            case RuleResultOk<List<CompoundNode>>(var value2) -> switch (this.rule.lex(s)) {
                case RuleResultErr<CompoundNode>(var error) -> new RuleResultErr<>(error);
                case RuleResultOk<CompoundNode>(CompoundNode value1) -> {
                    value2.add(value1);
                    yield new RuleResultOk<>(value2);
                }
            };
        };
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        final var compoundNodes = node.nodeLists().find(this.key).orElse(new ArrayList<>());
        return compoundNodes.stream().map(this.rule::generate).reduce(RuleResult.createFromValue(""), (result, result1) -> switch (result) {
            case RuleResultErr<String>(var error1) -> new RuleResultErr<>(error1);
            case RuleResultOk<String>(String value2) -> switch (result1) {
                case RuleResultErr<String>(var error) -> new RuleResultErr<>(error);
                case RuleResultOk<String>(String value1) -> new RuleResultOk<>(value2 + value1);
            };
        }, (_, next) -> next);
    }
}