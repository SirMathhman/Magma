package magma.app.io.target;

import magma.api.option.Option;

import java.io.IOException;

public interface Targets {
    Option<IOException> write(String output);
}
