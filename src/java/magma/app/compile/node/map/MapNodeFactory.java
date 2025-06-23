package magma.app.compile.node.map;

import magma.app.compile.node.property.CompoundNode;
import magma.app.compile.node.property.NodeFactory;

public class MapNodeFactory implements NodeFactory<CompoundNode> {
    @Override
    public CompoundNode createNode() {
        return new MapNode();
    }
}