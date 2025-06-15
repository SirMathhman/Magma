package magma.app;

public interface Node {
    Node withString(String key, String value);

    MaybeString findString(String key);
}
