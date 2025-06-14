package magma.app.compile.error.context;

import magma.app.compile.error.Context;
import magma.app.compile.node.CompoundNode;

public record NodeContext(CompoundNode node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
