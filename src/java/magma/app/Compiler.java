package magma.app;

import magma.api.result.Result;
import magma.app.error.FormattedError;

import java.util.Map;

public interface Compiler {
    Result<String, FormattedError> compile(Map<String, String> inputs);
}
