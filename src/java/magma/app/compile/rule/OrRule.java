package magma.app.compile.rule;

import magma.app.compile.node.Node;

import java.util.List;
import java.util.Optional;

public record OrRule(List<Rule> rules) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        return this.rules.stream()
                .map(rule -> rule.lex(input))
                .flatMap(Optional::stream)
                .findFirst();
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rules.stream()
                .map(rule -> rule.generate(node))
                .flatMap(Optional::stream)
                .findFirst();
    }
}
