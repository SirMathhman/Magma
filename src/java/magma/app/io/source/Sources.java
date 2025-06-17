package magma.app.io.source;

import magma.api.io.IOError;
import magma.api.result.Result;

import java.util.Set;

public interface Sources {
    Result<Set<Source>, IOError> collect();
}
