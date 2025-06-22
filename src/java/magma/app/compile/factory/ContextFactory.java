package magma.app.compile.factory;

import magma.app.compile.context.Context;
import magma.app.compile.node.DisplayNode;

interface ContextFactory<Node extends DisplayNode> {
    Context createStringContext(String context);

    Context createNodeContext(Node context);
}
