package magma.app.compile;

import magma.api.Tuple;
import magma.api.collect.list.ListLike;
import magma.api.collect.map.MapLike;
import magma.api.collect.stream.Joiner;
import magma.api.optional.OptionalLike;
import magma.app.compile.divide.DivideState;
import magma.app.compile.divide.MutableDivideState;
import magma.app.compile.lang.Lang;
import magma.app.compile.node.Node;

public class Compiler {
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
        return Compiler.divide(input).stream()
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

    private static ListLike<String> divide(final CharSequence input) {
        final DivideState state = new MutableDivideState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Compiler.fold(current, c);
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
