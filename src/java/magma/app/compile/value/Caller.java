package magma.app.compile.value;

import magma.api.option.Option;
import magma.app.ValueCompiler;

public sealed interface Caller permits ConstructionCaller, Value {
    default String generate() {
        return ValueCompiler.generateCaller(this);
    }

    Option<Value> findChild();
}
