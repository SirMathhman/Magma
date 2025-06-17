package magma.app.node;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Node {
    Node withString(String key, String value);

    Optional<String> findString(String key);

    Node merge(Node other);

    Stream<Map.Entry<String, String>> streamStrings();

    Node withNodeList(String key, List<Node> values);

    Optional<List<Node>> findNodeList(String key);

    String display();

    boolean is(String type);

    Node retype(String type);
}
