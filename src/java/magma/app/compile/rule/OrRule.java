package magma.app.compile.rule;

import magma.app.compile.node.NodeWithEverything;

import java.util.List;
import java.util.Optional;

public record OrRule(List<Rule> rules) implements Rule {
    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return this.rules.stream()
                .map(rule -> rule.lex(input))
                .flatMap(Optional::stream)
                .findFirst();
    }

    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return this.rules.stream()
                .map(rule -> rule.generate(node))
                .flatMap(Optional::stream)
                .findFirst();
    }
}
