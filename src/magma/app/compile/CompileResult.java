package magma.app.compile;

import magma.api.Result;

import java.util.Collections;
import java.util.List;

public record CompileResult<T>(Result<T, CompileException> result, List<CompileResult<T>> children) {
    public CompileResult(Result<T, CompileException> result) {
        this(result, Collections.emptyList());
    }

    public boolean isValid() {
        return result.isOk();
    }
}
