package magma.app.io.source;

import magma.api.io.IOError;
import magma.api.list.Sequence;
import magma.api.result.Result;

public interface Sources {
    Result<Sequence<Source>, IOError> collect();
}
