package magma;

import magma.api.error.WrappedError;
import magma.api.option.Option;

public interface Targets {
    Option<WrappedError> write(String output);
}
