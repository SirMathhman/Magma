package magma.app.compile.node;

import magma.api.collect.list.ListLike;

import java.util.Optional;

public interface NodeWithNodeLists<Node> {
    Node withNodeList(String key, ListLike<Node> values);

    Optional<ListLike<Node>> findNodeList(String key);
}
