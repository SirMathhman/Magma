package magma.app.compile;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;

import java.util.Map;

public interface Compiler {
    Result<String, FormattedError> compile(Map<String, String> inputs);
}
