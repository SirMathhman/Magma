package magma.app.node;

import magma.app.node.properties.Properties;

import java.util.List;

public interface NodeListNode {
    Properties<CompoundNode, List<CompoundNode>> nodeLists();
}
