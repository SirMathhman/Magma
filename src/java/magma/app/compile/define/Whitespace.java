package magma.app.compile.define;

import magma.app.compile.node.Node;

public record Whitespace() implements Node {
    @Override
    public boolean is(String type) {
        return "whitespace".equals(type);
    }
}
