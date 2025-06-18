package magma.app;

import magma.api.Tuple;
import magma.api.list.ListLike;
import magma.api.map.MapLike;
import magma.app.compile.Lang;
import magma.app.compile.divide.Divider;
import magma.app.compile.node.MapNodeWithEverything;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.OrRule;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compiler {
    public static String compile(MapLike<String, String> sourceMap) {
        return sourceMap.stream()
                .map(Compiler::compileSourceMapEntry)
                .collect(Collectors.joining());
    }

    private static String compileSourceMapEntry(Tuple<String, String> entry) {
        final var name = entry.left();
        final var input = entry.right();
        final var segments = Divider.divide(input);
        return compileRootSegments(segments, name);
    }

    private static String compileRootSegments(ListLike<String> segments, String name) {
        final var output = new StringBuilder();
        for (var i = 0; i < segments.size(); i++) {
            final var segment = segments.get(i);
            output.append(compileRootSegment(segment.strip(), name));
        }

        return output.toString();
    }

    private static String compileRootSegment(String input, String source) {
        return new OrRule<>(Lang.createJavaRootSegmentRule()).lex(input)
                .map(node -> modifyRootSegment(source, node))
                .orElse(Stream.empty())
                .map(Lang.createPlantRootSegmentRule()::generate)
                .flatMap(Optional::stream)
                .collect(Collectors.joining());
    }

    private static Stream<NodeWithEverything> modifyRootSegment(String source, NodeWithEverything node) {
        if (node.is("import"))
            return Stream.of(node.withString("source", source));

        return modifyStructureDefinition(node);
    }

    private static Stream<NodeWithEverything> modifyStructureDefinition(NodeWithEverything node) {
        final var retyped = node.is("record") ? node.retype("class") : node;

        final var maybeSupertype = retyped.findNode("supertype");
        if (maybeSupertype.isPresent()) {
            final var supertype = maybeSupertype.get();
            final var destination = findBaseType(supertype).orElse("");

            final var name = retyped.findString("name")
                    .orElse("");

            return Stream.of(retyped, new MapNodeWithEverything("implements").withString("source", name)
                            .withString("destination", destination));
        }

        return Stream.of(retyped);
    }

    private static Optional<String> findBaseType(NodeWithEverything node) {
        if (node.is("identifier"))
            return node.findString("value");
        if (node.is("generic"))
            return node.findString("base");
        return Optional.empty();
    }
}
