package magma.app.compile.node;

public class MapNodeFactory implements NodeFactory<NodeWithEverything> {
    @Override
    public NodeWithEverything create() {
        return new MapNode();
    }
}