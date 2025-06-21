package magma.app.node;

import magma.OptionalLike;

public interface Node {
    Node withString(String key, String value);

    OptionalLike<String> findString(String key);
}
