package magma.app.io.target;

import magma.api.io.IOError;
import magma.api.option.Option;

public interface Targets {
    Option<IOError> write(String output);
}
