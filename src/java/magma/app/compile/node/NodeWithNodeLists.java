package magma.app.compile.node;

import java.util.List;
import java.util.Optional;

public interface NodeWithNodeLists<Node> {
    Node withNodeList(String key, List<Node> values);

    Optional<List<Node>> findNodeList(String key);
}