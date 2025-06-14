package magma.app.node.core;

import magma.app.node.CompoundNode;
import magma.app.node.properties.Properties;

import java.util.List;

public interface NodeListNode {
    Properties<CompoundNode, List<CompoundNode>> nodeLists();
}
