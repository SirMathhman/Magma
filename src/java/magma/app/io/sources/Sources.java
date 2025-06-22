package magma.app.io.sources;

import magma.api.error.Error;
import magma.api.result.Result;

import java.util.Map;

public interface Sources {
    Result<Map<String, String>, Error> collect();
}
