package magma.result;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

/** Successful result value. */
public final class Ok<T> implements Result<T> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public Option<T> value() {
        return new Some<>(value);
    }

    @Override
    public Option<String> error() {
        return new None<>();
    }
}
