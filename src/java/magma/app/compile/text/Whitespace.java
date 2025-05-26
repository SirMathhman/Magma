package magma.app.compile.text;

import magma.api.option.None;
import magma.api.option.Option;
import magma.app.compile.define.Definition;
import magma.app.compile.define.Parameter;
import magma.app.compile.node.Node;

public record Whitespace() implements Parameter, Node {
    @Override
    public String generate() {
        return "";
    }

    @Override
    public Option<Definition> asDefinition() {
        return new None<Definition>();
    }

    @Override
    public boolean is(String type) {
        return "whitespace".equals(type);
    }
}
