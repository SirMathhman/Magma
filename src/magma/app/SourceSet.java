package magma.app;

import magma.api.Result;

import java.io.IOException;
import java.util.Set;

public interface SourceSet {
    Result<Set<Unit>, IOException> collectSources();
}
