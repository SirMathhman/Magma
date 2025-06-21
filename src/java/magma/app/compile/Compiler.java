package magma.app.compile;

import magma.api.Tuple;
import magma.api.collect.map.MapLike;
import magma.app.compile.divide.Divider;
import magma.app.compile.lang.Lang;
import magma.app.compile.node.Node;
import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.GenerateResultJoiner;

public class Compiler {
    private Compiler() {
    }

    public static GenerateResult compileEntries(final MapLike<String, String> sources) {
        return sources.stream()
                .map(Compiler::compileEntry)
                .collect(new GenerateResultJoiner());
    }

    private static GenerateResult compileEntry(final Tuple<String, String> entry) {
        final var name = entry.left();
        final var input = entry.right();
        return Compiler.compile(input, name)
                .prependSlice("class " + name + Lang.SEPARATOR);
    }

    private static GenerateResult compile(final CharSequence input, final String source) {
        return Divider.divide(input)
                .stream()
                .map(segment -> Compiler.compileRootSegment(segment, source))
                .collect(new GenerateResultJoiner());
    }

    private static GenerateResult compileRootSegment(final String input, final String name) {
        return Lang.createImportRule()
                .lex(input)
                .generate(node -> {
                    final Node withSource = node.withString("source", name);
                    return Lang.createDependencyRule()
                            .generate(withSource);
                });
    }
}
