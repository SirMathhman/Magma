package magma.app.io.source;

import magma.api.collect.set.SetLike;
import magma.api.io.IOError;
import magma.api.result.Result;

public interface Sources {
    Result<SetLike<Source>, IOError> collect();
}
