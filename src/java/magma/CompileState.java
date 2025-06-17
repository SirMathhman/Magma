package magma;

import magma.io.Location;

import java.util.ArrayList;
import java.util.List;

public record CompileState(Location current, List<Location> imports) {
    public CompileState(Location location) {
        this(location, new ArrayList<>());
    }

    public CompileState addImport(Location location) {
        this.imports.add(location);
        return this;
    }
}