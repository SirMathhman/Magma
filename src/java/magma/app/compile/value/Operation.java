package magma.app.compile.value;

import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.TypeCompiler;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.node.Node;

public record Operation(Node left, String targetInfix, Node right) implements Node {
    public String generate() {
        return ValueCompiler.generateValue(this.left) + " " + this.targetInfix + " " + ValueCompiler.generateValue(this.right);
    }

    public Option<Node> toNode() {
        return new Some<Node>(this);
    }

    public Node resolve(CompileState state) {
        return TypeCompiler.Unknown;
    }


    public boolean is(String type) {
        return false;
    }
}
