package magma.app.compile.node.attribute;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface NodeWithStrings<Node> {
    Node withString(String key, String value);

    Optional<String> findString(String key);

    Stream<Map.Entry<String, String>> streamStrings();
}
