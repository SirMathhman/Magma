package magma.app.compile.value;

import magma.api.option.Option;

public interface Argument extends Node {
    Option<Value> toValue();

    @Override
    default boolean is(String type) {
        return false;
    }
}
