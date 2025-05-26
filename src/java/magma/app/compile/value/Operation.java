package magma.app.compile.value;

import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.node.Node;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Operation(Node left, String targetInfix, Node right) implements Node {
    public String generate() {
        return ValueCompiler.generateValue(this.left) + " " + this.targetInfix + " " + ValueCompiler.generateValue(this.right);
    }

    public Option<Node> toNode() {
        return new Some<Node>(this);
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }


}
