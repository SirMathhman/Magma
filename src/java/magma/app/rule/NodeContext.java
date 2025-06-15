package magma.app.rule;

import magma.app.Context;
import magma.app.DisplayableNode;

public record NodeContext<Node extends DisplayableNode>(Node node) implements Context {
    @Override
    public String display() {
        return this.node.display();
    }
}
