package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

import java.util.Optional;

public class EmptyRule implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        return Optional.of(new MapNode());
    }

    @Override
    public Optional<String> generate(Node node) {
        return Optional.of("");
    }
}
