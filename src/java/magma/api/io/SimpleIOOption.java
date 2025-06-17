package magma.api.io;

import magma.api.option.Option;

public record SimpleIOOption(Option<IOError> maybeError) implements IOOption {
    public static IOOption empty() {
        return new SimpleIOOption(Option.empty());
    }

    public static IOOption of(IOError error) {
        return new SimpleIOOption(Option.of(error));
    }

    @Override
    public void printIfPresent() {
        this.maybeError.ifPresent(error -> System.err.println(error.display()));
    }
}
