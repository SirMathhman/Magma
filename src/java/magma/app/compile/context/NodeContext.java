package magma.app.compile.context;

import magma.app.compile.node.Node;

public record NodeContext(Node node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
