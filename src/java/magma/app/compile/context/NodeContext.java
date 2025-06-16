package magma.app.compile.context;

import magma.app.compile.node.DisplayableNode;

public record NodeContext(DisplayableNode node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
