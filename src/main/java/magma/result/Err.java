package magma.result;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

/** Error result with a message. */
public final class Err<T> implements Result<T> {
    private final String message;

    public Err(String message) {
        this.message = message;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public Option<T> value() {
        return new None<>();
    }

    @Override
    public Option<String> error() {
        return new Some<>(message);
    }
}
