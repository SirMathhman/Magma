package magma.app.compile.factory;

import magma.app.compile.context.Context;

interface ContextFactory<Node> {
    Context createStringContext(String context);

    Context createNodeContext(Node context);
}
