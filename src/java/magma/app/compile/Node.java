package magma.app.compile;

import magma.api.collect.iter.Collector;

import java.util.Map;

public interface Node extends MergeNode<Node>,
        TypeNode<Node>,
        DisplayNode,
        NodeWithStrings<Node>,
        NodeWithNodeLists<Node> {
    Node collect(Collector<Map.Entry<String, String>, Node> collector);
}
