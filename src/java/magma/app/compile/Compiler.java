package magma.app.compile;

import magma.api.Tuple;
import magma.api.collect.map.MapLike;
import magma.api.collect.stream.Joiner;
import magma.api.optional.OptionalLike;
import magma.app.compile.divide.Divider;
import magma.app.compile.lang.Lang;
import magma.app.compile.node.Node;

public class Compiler {
    private Compiler() {
    }

    public static String compileEntries(final MapLike<String, String> sources) {
        return sources.stream()
                .map(Compiler::compileEntry)
                .collect(new Joiner())
                .orElse("");
    }

    private static String compileEntry(final Tuple<String, String> entry) {
        final var name = entry.left();
        final var input = entry.right();
        final var compiled = Compiler.compile(input, name);
        return "class " + name + Lang.SEPARATOR + compiled;
    }

    private static String compile(final CharSequence input, final String source) {
        return Divider.divide(input)
                .stream()
                .map(segment -> Compiler.compileRootSegment(segment, source))
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
}
