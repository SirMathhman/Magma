package magma.app.compile.state;

import magma.api.option.Option;
import magma.app.io.location.Location;

public interface CompileState {
    String joinLocation();

    Option<Location> find(String childName);

    CompileState addImport(Location location);

    Location resolveSibling(String name);
}
