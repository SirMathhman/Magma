package magma.app.node;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Node {
    Node withString(String key, String value);

    Optional<String> findString(String key);

    Node merge(Node other);

    Stream<Map.Entry<String, String>> streamStrings();
}
