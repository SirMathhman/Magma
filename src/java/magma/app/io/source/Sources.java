package magma.app.io.source;

import magma.api.collect.map.MapLike;
import magma.api.io.IOError;
import magma.api.result.Result;

public interface Sources {
    Result<MapLike<String, String>, IOError> readSourceSet();
}
