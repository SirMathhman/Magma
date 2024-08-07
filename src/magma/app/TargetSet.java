package magma.app;

import magma.app.compile.CompileResult;

import java.util.Optional;

public interface TargetSet {
    Optional<ApplicationException> writeValue(Unit unit, CompileResult value);
}
