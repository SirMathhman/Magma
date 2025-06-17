package magma.app.compile;

import magma.api.collect.iter.Iter;

import java.util.Map;

public interface Node extends MergeNode<Node>, TypeNode<Node>, DisplayNode, NodeWithStrings<Node>, NodeWithNodeLists<Node> {
    Iter<Map.Entry<String, String>> streamStrings();
}
