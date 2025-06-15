package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;
import magma.app.maybe.node.PresentNode;
import magma.app.node.MapNode;

public record StringRule(String key) implements Rule<Node> {
    @Override
    public MaybeString generate(Node node) {
        return node.findString(this.key);
    }

    @Override
    public MaybeNode lex(String input) {
        return new PresentNode(new MapNode().withString(this.key(), input));
    }
}