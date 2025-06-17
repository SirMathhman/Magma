package magma.app.io;

import magma.api.result.Result;
import magma.app.error.ApplicationError;

import java.util.Map;

public interface Sources {
    Result<Map<Source, String>, ApplicationError> readAll();
}
