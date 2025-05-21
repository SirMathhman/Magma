package magma.app.compile.value;

import magma.api.option.Option;

public interface Caller {
    Option<Value> findChild();
}
