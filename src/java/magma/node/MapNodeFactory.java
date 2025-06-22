package magma.node;

public class MapNodeFactory implements NodeFactory {
    @Override
    public EverythingNode createNode() {
        return new MapNode();
    }
}