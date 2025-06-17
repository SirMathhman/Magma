package magma.app.io.target;

import jvm.io.JVMPaths;
import magma.api.io.IOOption;

public class PathTargets implements Targets {
    @Override
    public IOOption write(String output) {
        final var path = JVMPaths.get(".", "diagram.puml");
        return path.writeString(output);
    }
}