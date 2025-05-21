package magma.app.compile.value;

import magma.api.option.Option;

public sealed interface Caller permits ConstructionCaller, Value {
    String generate();

    Option<Value> findChild();
}
