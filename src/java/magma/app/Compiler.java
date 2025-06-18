package magma.app;

import magma.app.compile.Lang;
import magma.app.compile.MutableState;
import magma.app.compile.State;
import magma.app.compile.node.MapNodeWithEverything;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compiler {
    public static String compile(Map<String, String> sourceMap) {
        final StringBuilder output = new StringBuilder();
        for (var entry : sourceMap.entrySet()) {
            final var name = entry.getKey();
            final var input = entry.getValue();
            final var segments = divide(input);

            output.append(compileRootSegments(segments, name));
        }

        return output.toString();
    }

    private static List<String> divide(CharSequence input) {
        State current = new MutableState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(State current, char c) {
        final var appended = current.append(c);
        if (c == ';' && appended.isLevel())
            return appended.advance();
        if (c == '{')
            return appended.enter();
        if (c == '}')
            return appended.exit();
        return appended;
    }

    private static String compileRootSegments(Iterable<String> segments, String name) {
        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment.strip(), name).ifPresent(output::append);

        return output.toString();
    }

    private static Optional<String> compileRootSegment(String input, String source) {
        return getSource(input, source, Lang.createImportRule(), Lang.createDependencyRule());
    }

    private static Optional<String> getSource(String input, String source, Rule<NodeWithEverything> sourceRule, Rule<NodeWithEverything> targetRule) {
        return sourceRule.lex(input)
                .map(node -> node.withString("source", source))
                .flatMap(targetRule::generate)
                .or(() -> compileStructure(input));
    }

    private static Optional<String> compileStructure(String input) {
        final var contentStart = input.indexOf("{");
        if (contentStart < 0)
            return Optional.empty();
        final var stripped = input.substring(0, contentStart)
                .strip();

        return map(stripped, Lang.createStructureDefinitionsRule(), Lang.createPlantUMLRootSegmentRule());
    }

    private static Optional<String> map(String input, Rule<NodeWithEverything> sourceRule, Rule<NodeWithEverything> targetRule) {
        return Optional.of(sourceRule.lex(input)
                .map(Compiler::modifyStructureDefinition)
                .orElse(Stream.empty())
                .map(targetRule::generate)
                .flatMap(Optional::stream)
                .collect(Collectors.joining()));
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
