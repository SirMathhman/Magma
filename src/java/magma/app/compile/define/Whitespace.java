package magma.app.compile.define;

import magma.api.option.None;
import magma.api.option.Option;
import magma.app.compile.node.Node;

public record Whitespace() implements Parameter, Node {
    public String generate() {
        return "";
    }

    public Option<Definition> asDefinition() {
        return new None<Definition>();
    }

    @Override
    public boolean is(String type) {
        return "whitespace".equals(type);
    }
}
