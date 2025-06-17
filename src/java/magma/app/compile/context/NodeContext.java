package magma.app.compile.context;

import magma.app.compile.node.DisplayNode;

public final class NodeContext implements Context {
    private final DisplayNode node;

    public NodeContext(DisplayNode node) {
        this.node = node;
    }

    @Override
    public String display() {
        return this.node.display();
    }
}
