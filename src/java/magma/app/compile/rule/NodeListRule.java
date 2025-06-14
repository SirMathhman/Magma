package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.divide.FoldingDivider;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

import java.util.ArrayList;
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
        final var children = new FoldingDivider().divide(input).stream().map(segment -> this.rule.lex(segment).findValue()).flatMap(Optional::stream).toList();
        return OptionalLexResult.of(new PropertiesCompoundNode().nodeLists().with(this.key, children));
    }

    @Override
    public RuleResult<String> generate(CompoundNode node) {
        return OptionalLexResult.of(node.nodeLists().find(this.key).orElse(new ArrayList<>()).stream().map(source -> this.rule.generate(source).findValue()).flatMap(Optional::stream).collect(Collectors.joining()));
    }
}