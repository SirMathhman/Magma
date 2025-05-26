package magma.app.compile.value;

import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Invokable(Caller caller, Iterable<Value> args) implements Value {
    @Override
    public String generate() {
        var joinedArguments = this.joinArgs();
        return ValueCompiler.getString(this.caller) + "(" + joinedArguments + ")";
    }

    private String joinArgs() {
        return this.args.iter()
                .map((Value value) -> {
                    return ValueCompiler.getString(value);
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

    @Override
    public Option<String> generateAsEnumValue(String structureName) {
        return new Some<String>("\n\tstatic " + ValueCompiler.getString(this.caller) + ": " + structureName + " = new " + structureName + "(" + this.joinArgs() + ");");
    }
}
