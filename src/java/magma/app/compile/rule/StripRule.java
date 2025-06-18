package magma.app.compile.rule;

import magma.app.compile.node.Node;

import java.util.Optional;

public record StripRule(Rule rule) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.rule.lex(input.strip());
    }
}
