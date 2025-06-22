package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeResult;
import magma.string.StringResult;

interface ResultFactory {
    NodeResult<EverythingNode> fromNode(EverythingNode node);

    StringResult fromStringError(String message, EverythingNode node);

    StringResult fromString(String value);
}
