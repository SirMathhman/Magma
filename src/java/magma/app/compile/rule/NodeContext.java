package magma.app.compile.rule;

import magma.app.compile.Context;
import magma.app.compile.node.DisplayableNode;

public record NodeContext<Node extends DisplayableNode>(Node node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
