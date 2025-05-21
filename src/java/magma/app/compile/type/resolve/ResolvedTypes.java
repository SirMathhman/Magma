package magma.app.compile.type.resolve;

import magma.api.collect.list.List;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.app.Location;
import magma.app.Platform;
import magma.app.compile.CompileState;
import magma.app.compile.Dependency;
import magma.app.compile.Import;
import magma.app.compile.Registry;
import magma.app.io.Source;

public final class ResolvedTypes {
    public static CompileState getState(CompileState state, Location location) {
        var requestedNamespace = location.namespace();
        var requestedChild = location.name();

        var namespace = ResolvedTypes.fixNamespace(requestedNamespace, state.findContext().findNamespaceOrEmpty());
        if (state.findRegistry().doesImportExistAlready(requestedChild)) {
            return state;
        }

        var namespaceWithChild = namespace.addLast(requestedChild);
        var anImport = new Import(namespaceWithChild, requestedChild);
        return state.mapRegistry((Registry registry) -> registry.addImport(anImport));
    }

    public static CompileState addResolvedImportFromCache0(CompileState state, String base) {
        if (state.findStack().hasAnyStructureName(base)) {
            return state;
        }

        return state.findContext()
                .findSource(base)
                .map((Source source) -> {
                    Location location = source.createLocation();
                    return ResolvedTypes.getCompileState1(state, location)
                            .orElseGet(() -> ResolvedTypes.getState(state, location));
                })
                .orElse(state);
    }

    private static Option<CompileState> getCompileState1(CompileState immutableCompileState, Location location) {
        if (!immutableCompileState.findContext().hasPlatform(Platform.PlantUML)) {
            return new None<CompileState>();
        }

        var name = immutableCompileState.findContext().findNameOrEmpty();
        var dependency = new Dependency(name, location.name());
        if (immutableCompileState.findRegistry().containsDependency(dependency)) {
            return new None<CompileState>();
        }

        return new Some<CompileState>(immutableCompileState.mapRegistry((Registry registry1) -> registry1.addDependency(dependency)));
    }

    private static List<String> fixNamespace(List<String> requestedNamespace, List<String> thisNamespace) {
        if (thisNamespace.isEmpty()) {
            return requestedNamespace.addFirst(".");
        }

        return ResolvedTypes.addParentSeparator(requestedNamespace, thisNamespace.size());
    }

    private static List<String> addParentSeparator(List<String> newNamespace, int count) {
        var index = 0;
        var copy = newNamespace;
        while (index < count) {
            copy = copy.addFirst("..");
            index++;
        }

        return copy;
    }
}
