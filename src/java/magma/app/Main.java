package magma.app;

import magma.api.collect.list.ListLike;
import magma.api.collect.set.SetCollector;
import magma.api.collect.set.SetLike;
import magma.api.collect.stream.Joiner;
import magma.api.collect.stream.ResultCollector;
import magma.api.collect.stream.StreamLike;
import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.io.path.PathLikes;
import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.api.result.Result;
import magma.app.compile.Lang;
import magma.app.compile.divide.DivideState;
import magma.app.compile.divide.MutableDivideState;
import magma.app.compile.node.Node;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final var sourceRoot = PathLikes.get(".", "src", "java");
        sourceRoot.walk()
                .mapValue(Main::filter)
                .flatMapValue(Main::compileSources)
                .match(Main::write, Optionals::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static OptionalLike<IOError> write(final String output) {
        final var target = PathLikes.get(".", "diagram.puml");
        final var joined = String.join(Lang.SEPARATOR, "@startuml", "skinparam linetype ortho", output, "@enduml");
        return target.writeString(joined);
    }

    private static Result<String, IOError> compileSources(final SetLike<PathLike> sources) {
        return sources.stream()
                .map(Main::compileSource)
                .collect(new ResultCollector<>(new Joiner()))
                .mapValue(value -> value.orElse(""));
    }

    private static SetLike<PathLike> filter(final StreamLike<PathLike> files) {
        return files.filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .collect(new SetCollector<>());
    }

    private static Result<String, IOError> compileSource(final PathLike source) {
        final var fileName = source.getFileName()
                .asString();

        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        return source.readString()
                .mapValue(input -> {
                    final var compiled = Main.compile(input, name);
                    return "class " + name + Lang.SEPARATOR + compiled;
                });
    }

    private static String compile(final CharSequence input, final String source) {
        return Main.divide(input)
                .stream()
                .map(segment -> Main.compileRootSegment(segment, source))
                .flatMap(OptionalLike::stream)
                .collect(new Joiner())
                .orElse("");
    }

    private static OptionalLike<String> compileRootSegment(final String input, final String name) {
        return Lang.createImportRule()
                .lex(input)
                .flatMap(node -> {
                    final Node withSource = node.withString("source", name);
                    return Lang.createDependencyRule()
                            .generate(withSource);
                });
    }

    private static ListLike<String> divide(final CharSequence input) {
        final DivideState state = new MutableDivideState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c)
            return appended.advance();
        return appended;
    }
}
