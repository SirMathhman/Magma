package magma.node;

import magma.api.Tuple;

import java.util.Optional;
import java.util.stream.Stream;

public interface Node {
    Stream<Tuple<String, String>> streamStrings();

    Node withString(String key, String value);

    Optional<String> findString(String key);

    Node merge(Node other);
}
