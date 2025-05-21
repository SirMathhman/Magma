package magma.app.compile.value;

import magma.api.collect.list.Iterable;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.ValueCompiler;

public record Invokable(Caller caller, Iterable<Value> args) implements Value {
    @Override
    public Option<Value> toValue() {
        return new Some<Value>(this);
    }

    @Override
    public Option<Value> findChild() {
        return new None<Value>();
    }

    @Override
    public Option<String> generateAsEnumValue(String structureName) {
        return new Some<String>("\n\tstatic " + ValueCompiler.generateCaller(this.caller) + ": " + structureName + " = new " + structureName + "(" + ValueCompiler.joinArgs(this.args) + ");");
    }
}
