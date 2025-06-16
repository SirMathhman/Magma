package magma.app.compile.rule;

import java.util.List;
import java.util.Optional;

public record OrRule<Node, R extends Rule<Node>>(List<R> rules) implements Rule<Node> {
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
