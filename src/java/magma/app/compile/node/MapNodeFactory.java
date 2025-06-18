package magma.app.compile.node;

public class MapNodeFactory implements NodeFactory {
    @Override
    public NodeWithEverything create() {
        return new MapNode();
    }
}