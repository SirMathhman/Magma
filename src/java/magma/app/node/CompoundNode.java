package magma.app.node;

import magma.app.node.core.MergingNode;
import magma.app.node.core.NodeListNode;
import magma.app.node.core.StringNode;

public interface CompoundNode extends StringNode<CompoundNode>, NodeListNode<CompoundNode>, MergingNode<CompoundNode> {
}