package magma.app.compile.node;

import magma.app.compile.node.properties.NodeProperties;

import java.util.List;

public interface NodeWithNodeLists<Node> {
    NodeProperties<List<Node>, Node> nodeLists();
}
