package magma.app.compile;

import magma.api.Tuple;
import magma.api.collect.map.MapLike;
import magma.api.collect.stream.Joiner;
import magma.app.compile.divide.Divider;
import magma.app.compile.lang.Lang;
import magma.app.compile.node.Node;
import magma.app.compile.result.GenerateOk;
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
        final var compiled = Compiler.compile(input, name);
        return new GenerateOk("class " + name + Lang.SEPARATOR + compiled);
    }

    private static String compile(final CharSequence input, final String source) {
        return Divider.divide(input)
                .stream()
                .map(segment -> Compiler.compileRootSegment(segment, source))
                .collect(new Joiner())
                .orElse("");
    }

    private static String compileRootSegment(final String input, final String name) {
        return Lang.createImportRule()
                .lex(input)
                .generate(node -> {
                    final Node withSource = node.withString("source", name);
                    return Lang.createDependencyRule()
                            .generate(withSource);
                })
                .toResult()
                .findValue()
                .orElse("");
    }
}
