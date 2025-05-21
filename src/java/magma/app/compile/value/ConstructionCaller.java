package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;

public record ConstructionCaller(String right) implements Caller {
    @Override
    public String generate() {
        return "new " + this.right;
    }

    @Override
    public Option<Value> findChild() {
        return new None<Value>();
    }
}
