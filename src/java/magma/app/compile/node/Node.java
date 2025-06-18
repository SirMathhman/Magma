package magma.app.compile.node;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Node {
    Node withString(String key, String value);

    Optional<String> findString(String key);

    Node retype(String type);

    boolean is(String type);

    Node merge(Node other);

    Stream<Map.Entry<String, String>> streamStrings();

    Optional<Node> findNode(String key);

    Node withNode(String key, Node value);
}
