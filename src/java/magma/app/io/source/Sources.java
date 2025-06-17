package magma.app.io.source;

import magma.api.result.Result;

import java.io.IOException;
import java.util.Set;

public interface Sources {
    Result<Set<Source>, IOException> collect();
}
