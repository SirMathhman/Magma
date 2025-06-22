package magma.app;

import magma.api.error.WrappedError;
import magma.api.option.Option;

public interface Application {
    Option<WrappedError> run();
}
