package magma.node;

import java.util.Optional;

public interface Node {
    Node withString(String key, String value);

    Optional<String> find(String key);
}
