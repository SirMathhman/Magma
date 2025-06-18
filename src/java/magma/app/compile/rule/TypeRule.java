package magma.app.compile.rule;

import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record TypeRule(String type, Rule<NodeWithEverything> rule) implements Rule<NodeWithEverything> {
    @Override
    public Optional<String> generate(NodeWithEverything node) {
        if (node.is(this.type))
            return this.rule.generate(node);

        return Optional.empty();
    }

    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return this.rule.lex(input)
                .map(node -> node.retype(this.type));
    }
}
