package magma.app.compile;

import java.util.List;
import java.util.Optional;

public final class OptionalNodeRule implements Rule {
    private final String propertyKey;
    private final Rule rule;
    private final DisjunctionRule parser;

    public OptionalNodeRule(String propertyKey, Rule rule) {
        this.propertyKey = propertyKey;
        this.rule = rule;
        parser = new DisjunctionRule(List.of(rule, EmptyRule.EMPTY));
    }

    @Override
    public Optional<Node> parse(String input) {
        return parser.parse(input);
    }

    @Override
    public Optional<String> generate(Node node) {
        if (node.hasNode(propertyKey)) return rule.generate(node);
        return Optional.empty();
    }
}
