package magma.app.compile.factory;

import magma.app.compile.context.Context;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.DisplayNode;

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