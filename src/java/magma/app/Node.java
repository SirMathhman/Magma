package magma.app;

import magma.app.maybe.StringResult;

public interface Node {
    Node withString(String key, String value);

    StringResult<CompileError> findString(String key);
}
