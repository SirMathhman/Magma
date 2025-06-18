package magma.app.compile.rule;

import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record StripRule(Rule<NodeWithEverything> rule) implements Rule<NodeWithEverything> {
    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return this.rule.generate(node);
    }

    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return this.rule.lex(input.strip());
    }
}
