package magma.app.factory;

import magma.app.context.Context;
import magma.app.node.DisplayNode;

interface ContextFactory<Node extends DisplayNode> {
    Context createStringContext(String context);

    Context createNodeContext(Node context);
}
