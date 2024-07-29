package magma.app.io;

import java.io.IOException;
import java.util.Optional;

public interface TargetSet {
    Optional<IOException> writeTarget(Source source, String output);
}
