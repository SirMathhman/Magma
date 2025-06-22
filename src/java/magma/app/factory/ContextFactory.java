package magma.app.factory;

import magma.app.context.Context;

interface ContextFactory<Node> {
    Context createStringContext(String context);

    Context createNodeContext(Node context);
}
