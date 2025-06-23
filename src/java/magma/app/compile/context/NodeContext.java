package magma.app.compile.context;

import magma.app.compile.node.property.DisplayNode;

public record NodeContext(DisplayNode node) implements Context {
    @Override
    public String display() {
        return node.display();
    }
}
