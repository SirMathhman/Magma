package magma.app.compile.node;

import magma.app.compile.node.attribute.MergingNode;
import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.node.attribute.NodeWithStrings;
import magma.app.compile.node.attribute.NodeWithType;

public interface NodeWithEverything extends NodeWithStrings<NodeWithEverything>,
        NodeWithType<NodeWithEverything>,
        NodeWithNodes<NodeWithEverything>,
        MergingNode<NodeWithEverything> {
}