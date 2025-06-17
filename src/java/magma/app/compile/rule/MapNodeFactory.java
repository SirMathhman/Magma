package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public class MapNodeFactory implements NodeFactory<Node> {
    @Override
    public Node create() {
        return new MapNode();
    }
}