package magma.app;

public interface Node extends DisplayableNode {
    Node withString(String key, String value);

    StringResult<CompileError> findString(String key);
}