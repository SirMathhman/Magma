package magma.rule;

import magma.node.EverythingNode;

import java.util.List;
import java.util.Optional;

public record OrRule(List<Rule<EverythingNode>> rules) implements Rule<EverythingNode> {
    private Optional<EverythingNode> lex0(final String input) {
        return this.rules.stream().map(rule -> rule.lex(input).toOptional()).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rules.stream().map(rule -> rule.generate(node)).flatMap(Optional::stream).findFirst();
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }
}
