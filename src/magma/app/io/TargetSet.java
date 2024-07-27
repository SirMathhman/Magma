package magma.app.io;

import java.io.IOException;

public interface TargetSet {
    void writeTarget(Source source, String output) throws IOException;
}
