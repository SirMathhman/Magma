package magma.node.factory;

import magma.node.MapNode;

public class MapNodeFactory implements NodeFactory {
    @Override
    public MapNode createNode() {
        return new MapNode();
    }
}