package magma.app.compile.node;

import magma.api.list.ListLike;
import magma.app.compile.node.attribute.MergingNode;
import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.node.attribute.NodeWithStrings;
import magma.app.compile.node.attribute.NodeWithType;

import java.util.Optional;

public interface NodeWithEverything extends NodeWithStrings<NodeWithEverything>,
        NodeWithType<NodeWithEverything>,
        NodeWithNodes<NodeWithEverything>,
        MergingNode<NodeWithEverything> {
    NodeWithEverything withNodeList(String key, ListLike<NodeWithEverything> values);

    Optional<ListLike<NodeWithEverything>> findNodeList(String key);
}