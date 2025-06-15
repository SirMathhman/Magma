package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public class EmptyRule implements Rule {
    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return Optional.of(new MapNode());
    }

    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return Optional.of("");
    }
}
