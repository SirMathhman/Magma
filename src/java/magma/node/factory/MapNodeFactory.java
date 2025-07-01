package magma.node.factory;

import magma.node.EverythingNode;
import magma.node.MapNode;

public class MapNodeFactory implements NodeFactory<EverythingNode> {
    @Override
    public EverythingNode createNode() {
        return new MapNode();
    }
}