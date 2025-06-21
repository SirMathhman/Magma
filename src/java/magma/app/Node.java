package magma.app;

import magma.api.optional.OptionalLike;

public interface Node {
    Node withString(String key, String value);

    OptionalLike<String> findString(String key);
}
