package magma.app.io;

import java.io.IOException;

public interface TargetSet {
    void writeTarget(Unit unit, String output) throws IOException;
}
