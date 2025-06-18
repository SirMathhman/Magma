package magma.app.compile.node;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface NodeWithNodes<Node> {
    Optional<Node> findNode(String key);

    Node withNode(String key, Node value);

    Stream<Map.Entry<String, Node>> streamNodes();
}
