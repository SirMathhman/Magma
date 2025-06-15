package magma;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;
import magma.app.maybe.node.PresentNode;
import magma.app.maybe.string.PresentString;
import magma.app.node.MapNode;

public class EmptyRule implements Rule<Node, MaybeNode, MaybeString> {
    @Override
    public MaybeString generate(Node node) {
        return new PresentString("");
    }

    @Override
    public MaybeNode lex(String input) {
        return new PresentNode(new MapNode());
    }
}
