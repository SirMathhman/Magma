package magma.app.compile.value;

import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.ValueCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;

public record Invokable(Caller caller, Iterable<Value> args) implements Value {
    public String generate() {
        var joinedArguments = this.joinArgs();
        return ValueCompiler.generateCaller(this.caller) + "(" + joinedArguments + ")";
    }

    private String joinArgs() {
        return this.args.iter()
                .map((Value value) -> ValueCompiler.generateCaller(value))
                .collect(new Joiner(", "))
                .orElse("");
    }

    @Override
    public Option<Value> toValue() {
        return new Some<Value>(this);
    }

    @Override
    public Option<Value> findChild() {
        return new None<Value>();
    }

    public Type resolve(CompileState state) {
        return PrimitiveType.Unknown;
    }

    @Override
    public Option<String> generateAsEnumValue(String structureName) {
        return new Some<String>("\n\tstatic " + ValueCompiler.generateCaller(this.caller) + ": " + structureName + " = new " + structureName + "(" + this.joinArgs() + ");");
    }
}
