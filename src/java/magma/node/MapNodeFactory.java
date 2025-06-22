package magma.node;

public class MapNodeFactory implements NodeFactory<EverythingNode> {
    @Override
    public EverythingNode createNode() {
        return new MapNode();
    }
}