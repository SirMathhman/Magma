package magma;

import magma.optional.OptionalLike;

public interface Node {
    Node withString(String key, String value);

    OptionalLike<String> findString(String key);
}
