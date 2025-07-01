package magma.node;

public interface EverythingNode extends TypedNode<EverythingNode>, NodeWithNodeLists<EverythingNode>, NodeWithStrings<EverythingNode> {
    EverythingNode merge(EverythingNode other);
}
