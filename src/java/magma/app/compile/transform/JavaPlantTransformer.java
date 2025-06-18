package magma.app.compile.transform;

import magma.api.list.Foldable;
import magma.api.list.Lists;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public class JavaPlantTransformer implements Transformer {
    public static Foldable<NodeWithEverything> modifyRootSegment(String source, NodeWithEverything node) {
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

            return Lists.of(retyped,
                    new MapNode("implements").withString("source", name)
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

    @Override
    public NodeWithEverything transform(NodeWithEverything root, String name) {
        var newChildren = root.findNodeList("children")
                .orElse(Lists.empty())
                .fold(Lists.<NodeWithEverything>empty(), (list, root1) -> list.addAll(modifyRootSegment(name, root1)));

        return new MapNode().withNodeList("children", newChildren);
    }
}