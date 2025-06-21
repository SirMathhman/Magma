package magma.app.io;

import magma.api.collect.set.SetLike;
import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.result.Result;

public interface Sources {
    Result<SetLike<PathLike>, IOError> collect();
}
