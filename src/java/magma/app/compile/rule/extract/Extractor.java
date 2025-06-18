package magma.app.compile.rule.extract;

import magma.app.compile.rule.Rule;

import java.util.Optional;

public interface Extractor<Node, Value> {
    Node attach(Node node, String key, Value value);

    Optional<Value> fromString(String input, Rule<Node> rule);

    Optional<Value> fromNode(Node node, String key);

    Optional<String> generate(Value value, Rule<Node> rule);
}