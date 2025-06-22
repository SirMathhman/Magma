package magma.error;

import magma.node.DisplayNode;

public record NodeContext(DisplayNode node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
