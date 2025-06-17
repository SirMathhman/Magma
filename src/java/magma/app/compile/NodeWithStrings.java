package magma.app.compile;

import java.util.Optional;

public interface NodeWithStrings<Node> {
    Node withString(String key, String value);

    Optional<String> findString(String key);
}
