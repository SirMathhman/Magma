package magma.node.result;

import java.util.function.Function;

public interface Mapping<Value, Self> {
    Self map(Function<Value, Value> mapper);
}
