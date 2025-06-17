package magma.app.io.target;

import jvm.io.JVMPaths;
import magma.api.io.IOError;
import magma.api.option.Option;

public class PathTargets implements Targets {
    @Override
    public Option<IOError> write(String output) {
        final var path = JVMPaths.get(".", "diagram.puml");
        return path.writeString(output);
    }
}