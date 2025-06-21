package magma.app;

import magma.api.optional.Option;

public interface Application {
    Option<ApplicationError> run();
}
