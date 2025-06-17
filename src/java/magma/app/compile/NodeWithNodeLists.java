package magma.app.compile;

import magma.api.list.Iterable;

import java.util.Optional;

public interface NodeWithNodeLists<Node> {
    Node withNodeList(String key, Iterable<Node> values);

    Optional<Iterable<Node>> findNodeList(String key);
}