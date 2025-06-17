package magma.api.io;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;

import java.util.function.Consumer;

public record SimpleIOOption(Option<IOError> maybeError) implements IOOption {
    public static IOOption empty() {
        return new SimpleIOOption(new None<>());
    }

    public static IOOption of(IOError error) {
        return new SimpleIOOption(new Some<>(error));
    }

    @Override
    public void ifPresent(Consumer<IOError> consumer) {
        this.maybeError.ifPresent(consumer);
    }
}
