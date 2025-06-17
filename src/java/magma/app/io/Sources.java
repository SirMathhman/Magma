package magma.app.io;

import magma.api.Error;
import magma.api.result.Result;

import java.util.Map;

public interface Sources {
    Result<Map<Source, String>, Error> readAll();
}
