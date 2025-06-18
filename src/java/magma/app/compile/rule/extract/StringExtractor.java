package magma.app.compile.rule.extract;

import magma.app.compile.node.attribute.NodeWithStrings;

import java.util.Optional;

public class StringExtractor<Node extends NodeWithStrings<Node>> implements Extractor<Node, String> {
    @Override
    public Node attach(Node node, String key, String s) {
        return node.withString(key, s);
    }

    @Override
    public Optional<String> lex(String input) {
        return Optional.of(input);
    }

    @Override
    public Optional<String> fromNode(Node node, String key) {
        return node.findString(key);
    }

    @Override
    public Optional<String> generate(String s) {
        return Optional.of(s);
    }
}
