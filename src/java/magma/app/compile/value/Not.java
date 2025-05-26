package magma.app.compile.value;

import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.compile.CompileState;
import magma.app.compile.node.Node;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Not(String child) implements Node {
    public String generate() {
        return this.child;
    }

    public Option<Node> toNode() {
        return new Some<Node>(this);
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }


    public boolean is(String type) {
        return false;
    }
}
