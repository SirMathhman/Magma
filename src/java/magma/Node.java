package magma;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Node {
    Node withString(String key, String value);

    Optional<String> findString(String key);

    Stream<Map.Entry<String, String>> stream();

    Node merge(Node other);
}
