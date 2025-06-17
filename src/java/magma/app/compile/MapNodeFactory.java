package magma.app.compile;

public class MapNodeFactory implements NodeFactory<Node> {
    @Override
    public Node create() {
        return new MapNode();
    }
}