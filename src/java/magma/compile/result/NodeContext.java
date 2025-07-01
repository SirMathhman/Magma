package magma.compile.result;

import magma.error.context.Context;
import magma.node.EverythingNode;

public record NodeContext(EverythingNode context) implements Context {
    @Override
    public String display() {
        return this.context.display();
    }
}
