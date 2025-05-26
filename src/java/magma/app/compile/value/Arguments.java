package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;

public class Arguments {
    public static Option<Value> toValue(Argument argument) {
        return argument instanceof Value value
                ? new Some<>(value)
                : new None<>();
    }
}
