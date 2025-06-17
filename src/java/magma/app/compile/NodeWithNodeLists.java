package magma.app.compile;

import magma.api.List;

import java.util.Optional;

public interface NodeWithNodeLists<Node> {
    Node withNodeList(String key, List<Node> values);

    Optional<List<Node>> findNodeList(String key);
}