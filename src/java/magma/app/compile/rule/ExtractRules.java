package magma.app.compile.rule;

import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.node.attribute.NodeWithStrings;
import magma.app.compile.rule.extract.NodeExtractor;
import magma.app.compile.rule.extract.NodeListExtractor;
import magma.app.compile.rule.extract.StringExtractor;

public class ExtractRules {
    public static <Node extends NodeWithNodes<Node>> Rule<Node> Node(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new NodeExtractor<>(rule));
    }

    public static <Node extends NodeWithStrings<Node>> Rule<Node> createStringRule(String key, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new StringExtractor<>());
    }

    public static <Node extends NodeWithNodeLists<Node>> Rule<Node> NodeList(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new NodeListExtractor<>(rule));
    }
}
