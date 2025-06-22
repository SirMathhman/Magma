package magma.error;

import magma.node.Node;

public record NodeContext(Node node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
