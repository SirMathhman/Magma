package magma.app.io.target;

import java.io.IOException;
import java.util.Optional;

public interface Targets {
    Optional<IOException> write(String output);
}
