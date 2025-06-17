package magma.api.io;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;

public record SimpleIOOption(Option<IOError> maybeError) implements IOOption {
    public static IOOption empty() {
        return new SimpleIOOption(new None<>());
    }

    public static IOOption of(IOError error) {
        return new SimpleIOOption(new Some<>(error));
    }

    @Override
    public void printIfPresent() {
        this.maybeError.ifPresent(error -> System.err.println(error.display()));
    }
}
