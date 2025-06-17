package magma.app.compile;

import magma.api.list.Streamable;

import java.util.Optional;

public interface NodeWithNodeLists<Node> {
    Node withNodeList(String key, Streamable<Node> values);

    Optional<Streamable<Node>> findNodeList(String key);
}