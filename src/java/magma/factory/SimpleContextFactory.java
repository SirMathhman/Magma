package magma.factory;

import magma.error.Context;
import magma.error.NodeContext;
import magma.error.StringContext;
import magma.node.DisplayNode;

public class SimpleContextFactory<Node extends DisplayNode> implements ContextFactory<Node> {
    @Override
    public Context createStringContext(final String context) {
        return new StringContext(context);
    }

    @Override
    public Context createNodeContext(final Node context) {
        return new NodeContext(context);
    }
}