package magma.api.io;

import magma.api.option.Option;
import magma.api.option.Options;

public record SimpleIOOption(Option<IOError> maybeError) implements IOOption {
    public static IOOption empty() {
        return new SimpleIOOption(Options.empty());
    }

    public static IOOption of(IOError error) {
        return new SimpleIOOption(Options.of(error));
    }

    @Override
    public void printIfPresent() {
        this.maybeError.ifPresent(error -> System.err.println(error.display()));
    }
}
