package magma.app.compile.node.attribute;

import magma.api.Tuple;

import java.util.Optional;
import java.util.stream.Stream;

public interface NodeWithNodes<Node> {
    Optional<Node> findNode(String key);

    Node withNode(String key, Node value);

    Stream<Tuple<String, Node>> streamNodes();
}
