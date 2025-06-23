package magma.app.compile.context;

import magma.app.compile.node.property.DisplayNode;

public class SimpleContextFactory<Node extends DisplayNode> implements ContextFactory<Node> {
    private record StringContext(String value) implements Context {
        @Override
        public String display() {
            return value;
        }
    }

    private record NodeContext(DisplayNode node) implements Context {
        @Override
        public String display() {
            return node.display();
        }
    }

    @Override
    public Context createStringContext(final String context) {
        return new StringContext(context);
    }

    @Override
    public Context createNodeContext(final Node context) {
        return new NodeContext(context);
    }
}