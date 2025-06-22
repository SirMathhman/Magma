package magma.rule;

import magma.node.EverythingNode;
import magma.node.MapNode;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public interface ResultFactory {
    NodeResult<EverythingNode> fromNode(MapNode node);

    StringResult fromStringError(String message, EverythingNode node);

    StringResult fromString(String value);
}
