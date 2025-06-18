package magma.app.compile.rule;

import java.util.Optional;

public interface Extractor<Node, Value> {
    Node attach(Node node, String key, Value value);

    Optional<Value> lex(String input);

    Optional<Value> fromNode(Node node, String key);

    Optional<String> generate(Value value);
}