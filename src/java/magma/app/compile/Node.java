package magma.app.compile;

import magma.app.compile.node.DisplayableNode;

import java.util.List;

public interface Node extends DisplayableNode {
    Node withString(String key, String value);

    StringResult<CompileError> findString(String key);

    NodeListResult<Node, CompileError, NodeResult<Node, CompileError>> findNodeList(String key);

    Node withNodeList(String key, List<Node> values);
}