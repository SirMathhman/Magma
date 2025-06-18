package magma.app;

import magma.api.Tuple;
import magma.api.list.ListLike;
import magma.api.list.Lists;
import magma.api.map.MapLike;
import magma.app.compile.Lang;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;
import java.util.stream.Collectors;

public class Compiler {
    public static String compile(MapLike<String, String> sourceMap) {
        return sourceMap.stream()
                .map(Compiler::compileSourceMapEntry)
                .flatMap(Optional::stream)
                .collect(Collectors.joining());
    }

    private static Optional<String> compileSourceMapEntry(Tuple<String, String> entry) {
        final var name = entry.left();
        final var input = entry.right();
        return Lang.createJavaRootRule()
                .lex(input)
                .map(root -> modifyRoot(root, name))
                .flatMap(root -> Lang.createPlantRootRule()
                        .generate(root));
    }

    private static NodeWithEverything modifyRoot(NodeWithEverything root, String name) {
        ListLike<NodeWithEverything> oldChildren = root.findNodeList("children")
                .orElse(Lists.empty());

        var newChildren = Lists.<NodeWithEverything>empty();
        for (var i = 0; i < oldChildren.size(); i++) {
            final var oldChild = oldChildren.get(i);
            newChildren = newChildren.addAll(modifyRootSegment(name, oldChild));
        }

        return new MapNode().withNodeList("children", newChildren);
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

            return Lists.of(retyped, new MapNode("implements").withString("source", name)
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
