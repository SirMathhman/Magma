package magma.app.context;

import magma.app.node.DisplayNode;

public record NodeContext(DisplayNode node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
