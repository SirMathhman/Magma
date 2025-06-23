package magma.app.compile.context;

import magma.app.compile.node.property.DisplayNode;

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