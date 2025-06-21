package magma;

public interface Node {
    Node withString(String key, String value);

    OptionalLike<String> findString(String key);
}
