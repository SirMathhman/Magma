package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;

public record ConstructionCaller(String type) implements Caller {
    public String generate() {
        return "new " + this.type;
    }

    @Override
    public Option<Value> findChild() {
        return new None<Value>();
    }
}
