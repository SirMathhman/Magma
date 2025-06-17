package magma.app.compile;

import java.util.Map;
import java.util.stream.Stream;

public interface Node extends MergingNode<Node>, TypeNode<Node>, DisplayNode, NodeWithStrings<Node>, NodeWithNodeLists<Node> {
    Stream<Map.Entry<String, String>> streamStrings();
}
