package magma.app.compile.state;

import jvm.list.JVMLists;
import magma.api.list.ListLike;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.io.location.Location;

public final class SimpleCompileState implements CompileState {
    private final Location current;
    private final ListLike<Location> imports;

    public SimpleCompileState(Location current, ListLike<Location> imports) {
        this.current = current;
        this.imports = imports;
    }

    public SimpleCompileState(Location location) {
        this(location, JVMLists.empty());
    }

    @Override
    public String joinLocation() {
        return this.current.join();
    }

    @Override
    public Option<Location> find(String childName) {
        for (var i = 0; i < this.imports.size(); i++) {
            var anImport = this.imports.get(i);
            if (anImport.isNamed(childName))
                return new Some<>(anImport);
        }

        return new None<>();
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