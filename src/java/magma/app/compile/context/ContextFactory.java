package magma.app.compile.context;

interface ContextFactory<Node> {
    Context createStringContext(String context);

    Context createNodeContext(Node context);
}
