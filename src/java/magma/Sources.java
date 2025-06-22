package magma;

import magma.api.error.WrappedError;
import magma.api.result.Result;

import java.util.Map;

public interface Sources {
    Result<Map<String, String>, WrappedError> collect();
}
