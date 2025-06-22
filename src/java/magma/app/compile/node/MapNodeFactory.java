package magma.app.compile.node;

public class MapNodeFactory implements NodeFactory<EverythingNode> {
    @Override
    public EverythingNode createNode() {
        return new MapNode();
    }
}