package magma.node.result;

import magma.node.EverythingNode;

public interface NodeListResult {
    NodeListResult add(NodeResult<EverythingNode> other);

    NodeResult<EverythingNode> toNode(String key);
}
