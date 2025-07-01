package magma.error.context;

import magma.node.EverythingNode;

public record NodeContext(EverythingNode context) implements Context {
    @Override
    public String display() {
        return this.context.display();
    }
}
