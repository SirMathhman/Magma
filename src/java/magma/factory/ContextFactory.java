package magma.factory;

import magma.error.Context;
import magma.node.DisplayNode;

interface ContextFactory<Node extends DisplayNode> {
    Context createStringContext(String context);

    Context createNodeContext(Node context);
}
