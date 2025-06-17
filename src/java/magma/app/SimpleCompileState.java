package magma.app;

import magma.app.io.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SimpleCompileState implements CompileState {
    private final Location current;
    private final List<Location> imports;

    public SimpleCompileState(Location current, List<Location> imports) {
        this.current = current;
        this.imports = imports;
    }

    public SimpleCompileState(Location location) {
        this(location, new ArrayList<>());
    }

    @Override
    public String joinLocation() {
        return this.current.join();
    }

    @Override
    public Optional<Location> find(String childName) {
        for (var anImport : this.imports)
            if (anImport.isNamed(childName))
                return Optional.of(anImport);

        return Optional.empty();
    }

    @Override
    public CompileState addImport(Location location) {
        this.imports.add(location);
        return this;
    }

    @Override
    public Location resolveSibling(String name) {
        return this.current.resolveSibling(name);
    }
}