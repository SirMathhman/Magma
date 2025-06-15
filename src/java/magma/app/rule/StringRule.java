package magma.app.rule;

import magma.app.MaybeNode;
import magma.app.Rule;
import magma.app.node.MapNode;
import magma.app.node.PresentNode;

public record StringRule(String key) implements Rule {
    @Override
    public MaybeNode lex(String input) {
        return new PresentNode(new MapNode().withString(this.key(), input));
    }
}