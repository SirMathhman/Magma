package magma.app.factory;

import magma.app.context.Context;
import magma.app.context.NodeContext;
import magma.app.context.StringContext;
import magma.app.node.DisplayNode;

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