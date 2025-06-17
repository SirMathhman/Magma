package magma.app.compile;

import magma.api.result.Result;
import magma.app.error.ApplicationError;
import magma.app.io.Source;

import java.util.Map;

public interface Compiler {
    Result<String, ApplicationError> compile(Map<Source, String> inputs);
}
