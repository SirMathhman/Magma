package magma.node.result;

import magma.string.result.StringResult;

public interface NodeListResult<Node, Error> {
    NodeListResult<Node, Error> add(NodeResult<Node, Error, StringResult<Error>> other);

    NodeResult<Node, Error, StringResult<Error>> toNode(String key);
}
