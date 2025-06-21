package magma.app.node;

import magma.app.optional.OptionalLike;

public interface Node {
    Node withString(String key, String value);

    OptionalLike<String> findString(String key);
}
