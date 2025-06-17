package magma.app.compile.node;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Node extends MergingNode<Node>, TypedNode<Node>, DisplayNode {
    Node withString(String key, String value);

    Optional<String> findString(String key);

    Stream<Map.Entry<String, String>> streamStrings();

    Node withNodeList(String key, List<Node> values);

    Optional<List<Node>> findNodeList(String key);
}
