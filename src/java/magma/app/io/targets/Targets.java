package magma.app.io.targets;

import magma.api.error.Error;
import magma.api.option.Option;

public interface Targets {
    Option<Error> write(String output);
}
