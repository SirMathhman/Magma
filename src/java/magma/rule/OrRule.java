package magma.rule;

import magma.node.EverythingNode;

import java.util.List;
import java.util.Optional;

public record OrRule(List<Rule<EverythingNode>> rules) implements Rule<EverythingNode> {
    @Override
    public Optional<EverythingNode> lex(final String input) {
        return this.rules.stream().map(rule -> rule.lex(input)).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rules.stream().map(rule -> rule.generate(node)).flatMap(Optional::stream).findFirst();
    }
}
