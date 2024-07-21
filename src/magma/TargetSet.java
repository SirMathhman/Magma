package magma;

import java.io.IOException;

public interface TargetSet {
    void writeTarget(CompileUnit unit) throws IOException;
}
