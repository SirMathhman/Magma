package magma.build;

import magma.api.result.Result;
import magma.build.compile.error.CompileException;

import java.util.List;

public interface Unit {
    Result<String, CompileException> read();

    List<String> computeNamespace();

    String computeName();
}
