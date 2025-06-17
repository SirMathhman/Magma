package magma.app.io.source;

import magma.api.io.IOError;
import magma.api.list.ListLike;
import magma.api.result.Result;

public interface Sources {
    Result<ListLike<Source>, IOError> collect();
}
