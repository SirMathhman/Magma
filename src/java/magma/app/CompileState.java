package magma.app;

import magma.app.io.location.Location;

import java.util.Optional;

public interface CompileState {
    String joinLocation();

    Optional<Location> find(String childName);

    CompileState addImport(Location location);

    Location resolveSibling(String name);
}
