package magma;

import magma.app.node.Node;

public record NodeContext(Node node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
