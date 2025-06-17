package magma.app.rule;

import magma.app.node.Node;

import java.util.Optional;

public record StripRule(Rule rule) implements Rule {
    @Override
    public Optional<Node> lex(String segment) {
        return this.rule.lex(segment.strip());
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node);
    }
}