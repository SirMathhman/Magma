package magma.app.compile.node;

public interface NodeWithEverything extends NodeWithStrings<NodeWithEverything>,
        NodeWithType<NodeWithEverything>,
        NodeWithNodes<NodeWithEverything>,
        MergingNode<NodeWithEverything> {
}