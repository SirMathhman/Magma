package magma.app.compile.context;

public interface ContextFactory<Node> {
    Context createStringContext(String context);

    Context createNodeContext(Node context);
}
