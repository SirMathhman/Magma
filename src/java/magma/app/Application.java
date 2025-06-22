package magma.app;

import magma.api.error.Error;
import magma.api.option.Option;

public interface Application {
    Option<Error> run();
}
