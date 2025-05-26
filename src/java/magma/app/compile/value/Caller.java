package magma.app.compile.value;

import magma.api.option.None;
import magma.api.option.Option;

public interface Caller {
    String generate();

    default Option<Value> findChild() {
        return new None<Value>();
    }
}
