package magma.app.compile.value;

import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.node.Node;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Invokable(Node node, Iterable<Node> args) implements Node {
    public String generate() {
        var joinedArguments = this.joinArgs();
        return ValueCompiler.getString(this.node) + "(" + joinedArguments + ")";
    }

    public String joinArgs() {
        return this.args.iter()
                .map((Node value) -> {
                    return ValueCompiler.generateValue(value);
                })
                .collect(new Joiner(", "))
                .orElse("");
    }

    public Option<Node> toNode() {
        return new Some<Node>(this);
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }

}
