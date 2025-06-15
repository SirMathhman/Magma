package magma.app.compile;

import magma.app.compile.node.DisplayableNode;

public interface Node extends DisplayableNode {
    Node withString(String key, String value);

    StringResult<CompileError> findString(String key);
}