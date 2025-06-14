package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.divide.FoldingDivider;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

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
        return new FoldingDivider().divide(input).stream().map(this.rule::lex).reduce(OptionalLexResult.of(new ArrayList<>()), this::fold, (_, next) -> next).map(children -> new PropertiesCompoundNode().nodeLists().with(this.key, children));
    }

    private RuleResult<List<CompoundNode>> fold(RuleResult<List<CompoundNode>> result0, RuleResult<CompoundNode> result) {
        return result0.flatMap(inner -> result.map(inner0 -> {
            inner.add(inner0);
            return inner;
        }));
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        final var children = node.nodeLists().find(this.key).orElse(new ArrayList<>());
        return children.stream().map(this.rule::generate).reduce(OptionalLexResult.of(""), (current, next) -> current.flatMap(inner -> next.map(inner0 -> inner + inner0)), (_, next) -> next);
    }
}