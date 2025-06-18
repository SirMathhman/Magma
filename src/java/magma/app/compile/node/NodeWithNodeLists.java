package magma.app.compile.node;

import magma.api.collect.fold.Foldable;

import java.util.Optional;

public interface NodeWithNodeLists<Node> {
    Node withNodeList(String key, Foldable<Node> values);

    Optional<Foldable<Node>> findNodeList(String key);
}
