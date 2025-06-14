package magma.app.compile.node;

import magma.app.compile.node.core.MergingNode;
import magma.app.compile.node.core.NodeListNode;
import magma.app.compile.node.core.StringNode;

public interface CompoundNode extends StringNode<CompoundNode>, NodeListNode<CompoundNode>, MergingNode<CompoundNode>, DisplayableNode, TypedNode<CompoundNode> {
}