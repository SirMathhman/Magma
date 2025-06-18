package magma.app;

import magma.api.Tuple;
import magma.api.list.ListLike;
import magma.api.list.Lists;
import magma.api.map.MapLike;
import magma.app.compile.Lang;
import magma.app.compile.divide.Divider;
import magma.app.compile.node.MapNodeWithEverything;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;
import java.util.stream.Collectors;

public class Compiler {
    public static String compile(MapLike<String, String> sourceMap) {
        return sourceMap.stream()
                .map(Compiler::compileSourceMapEntry)
                .collect(Collectors.joining());
    }

    private static String compileSourceMapEntry(Tuple<String, String> entry) {
        final var name = entry.left();
        final var input = entry.right();
        final var oldChildren = lex(input);
        final var newChildren = modifyRoot(name, oldChildren);
        return generate(newChildren);
    }

    private static ListLike<NodeWithEverything> lex(CharSequence input) {
        final var segments = Divider.divide(input);
        var oldChildren = Lists.<NodeWithEverything>empty();
        for (var i = 0; i < segments.size(); i++) {
            final var segment = segments.get(i);
            oldChildren = Lang.createJavaRootSegmentRule()
                    .lex(segment)
                    .map(oldChildren::add)
                    .orElse(oldChildren);
        }
        return oldChildren;
    }

    private static ListLike<NodeWithEverything> modifyRoot(String name, ListLike<NodeWithEverything> oldChildren) {
        var newChildren = Lists.<NodeWithEverything>empty();
        for (var i = 0; i < oldChildren.size(); i++) {
            final var oldChild = oldChildren.get(i);
            newChildren = newChildren.addAll(modifyRootSegment(name, oldChild));
        }
        return newChildren;
    }

    private static String generate(ListLike<NodeWithEverything> newChildren) {
        var output = new StringBuilder();
        for (var i = 0; i < newChildren.size(); i++) {
            final var newChild = newChildren.get(i);
            output = Lang.createPlantRootSegmentRule()
                    .generate(newChild)
                    .map(output::append)
                    .orElse(output);
        }

        return output.toString();
    }

    private static ListLike<NodeWithEverything> modifyRootSegment(String source, NodeWithEverything node) {
        if (node.is("import"))
            return Lists.of(node.retype("dependency")
                    .withString("source", source));

        final var retyped = node.is("record") ? node.retype("class") : node;

        final var maybeSupertype = retyped.findNode("supertype");
        if (maybeSupertype.isPresent()) {
            final var supertype = maybeSupertype.get();
            final var destination = findBaseType(supertype).orElse("");

            final var name = retyped.findString("name")
                    .orElse("");

            return Lists.of(retyped, new MapNodeWithEverything("implements").withString("source", name)
                            .withString("destination", destination));
        }

        return Lists.of(retyped);
    }

    private static Optional<String> findBaseType(NodeWithEverything node) {
        if (node.is("identifier"))
            return node.findString("value");
        if (node.is("generic"))
            return node.findString("base");
        return Optional.empty();
    }
}
