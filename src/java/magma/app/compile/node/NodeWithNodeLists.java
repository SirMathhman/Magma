package magma.app.compile.node;

import magma.api.list.Sequence;

import java.util.Optional;

public interface NodeWithNodeLists<Node> {
    Node withNodeList(String key, Sequence<Node> values);

    Optional<Sequence<Node>> findNodeList(String key);
}
