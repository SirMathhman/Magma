package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.List;
import java.util.Optional;

public record OrRule(List<Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>> rules) implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private Optional<EverythingNode> lex0(final String input) {
        return this.rules.stream().map(rule -> rule.lex(input).toOptional()).flatMap(Optional::stream).findFirst();
    }

    private Optional<String> generate0(final EverythingNode node) {
        return this.rules.stream().map(rule -> rule.generate(node).toOptional()).flatMap(Optional::stream).findFirst();
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.generate0(node).<StringResult>map(StringOk::new).orElseGet(StringErr::new);
    }
}
