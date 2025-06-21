package magma.app;

import magma.api.optional.OptionalLike;

public interface Application {
    OptionalLike<ApplicationError> run();
}
