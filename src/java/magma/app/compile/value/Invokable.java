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

public record Invokable(Node node, Iterable<Value> args) implements Value {
    public String generate() {
        var joinedArguments = this.joinArgs();
        return ValueCompiler.getString(this.node) + "(" + joinedArguments + ")";
    }

    public String joinArgs() {
        return this.args.iter()
                .map((Value value) -> {
                    return ValueCompiler.generateValue(value);
                })
                .collect(new Joiner(", "))
                .orElse("");
    }

    public Option<Value> toValue() {
        return new Some<Value>(this);
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }

}
