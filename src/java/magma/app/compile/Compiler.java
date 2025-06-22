package magma.app.compile;

import magma.api.Tuple;
import magma.api.collect.list.ListCollector;
import magma.api.collect.list.ListLike;
import magma.api.collect.map.MapLike;
import magma.app.compile.divide.Divider;
import magma.app.compile.lang.Lang;
import magma.app.compile.node.Node;
import magma.app.compile.result.GenerateResultJoiner;
import magma.app.compile.result.NodeListOk;
import magma.app.compile.result.NodeListResult;
import magma.app.compile.result.NodeListResultCollector;
import magma.app.compile.result.StringResult;

public class Compiler {
    private Compiler() {
    }

    public static StringResult compileEntries(final MapLike<String, String> sources) {
        return sources.stream()
                .map(Compiler::compileEntry)
                .collect(new GenerateResultJoiner());
    }

    private static StringResult compileEntry(final Tuple<String, String> entry) {
        final var name = entry.left();
        final var input = entry.right();
        return Compiler.compile(input, name)
                .prependSlice("class " + name + Lang.SEPARATOR);
    }

    private static StringResult compile(final CharSequence input, final String source) {
        return Divider.divide(input)
                .stream()
                .map(segment -> Lang.createImportRule()
                        .lex(segment))
                .collect(new NodeListResultCollector())
                .map(children -> Compiler.modifyChildren(children, source))
                .generate(list -> Lang.createDependencyRule()
                        .generate(list));
    }

    private static NodeListResult modifyChildren(final ListLike<Node> children, final String source) {
        return new NodeListOk(children.stream()
                .map(node -> node.withString("source", source))
                .collect(new ListCollector<>()));
    }
}
