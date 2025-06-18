package magma.app;

import magma.app.compile.LocateRule;
import magma.app.compile.MutableState;
import magma.app.compile.OrRule;
import magma.app.compile.PrefixRule;
import magma.app.compile.Rule;
import magma.app.compile.State;
import magma.app.compile.StringRule;
import magma.app.compile.SuffixRule;
import magma.app.compile.TypeRule;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

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
        return createImportRule().lex(input)
                .flatMap(node -> createDependencyRule().generate(node.withString("source", source)))
                .or(() -> compileStructure(input));
    }

    private static Rule createDependencyRule() {
        return new SuffixRule(LocateRule.Last(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    private static Rule createImportRule() {
        return new PrefixRule("import ",
                new SuffixRule(LocateRule.Last(new StringRule("temp"), ".", new StringRule("destination")), ";"));
    }

    private static Optional<String> compileStructure(String input) {
        final var contentStart = input.indexOf("{");
        if (contentStart < 0)
            return Optional.empty();
        final var stripped = input.substring(0, contentStart)
                .strip();

        return Optional.of(createStructureDefinitionsRule().lex(stripped)
                .map(Compiler::modifyStructureDefinition)
                .orElse(Stream.empty())
                .map(node -> createPlantUMLRootSegmentRule().generate(node))
                .flatMap(Optional::stream)
                .collect(Collectors.joining()));
    }

    private static Rule createPlantUMLRootSegmentRule() {
        return new SuffixRule(new OrRule(List.of(createPlantUMLClassesRule(), createiplementsRule())), "\n");
    }

    private static Rule createiplementsRule() {
        return new TypeRule("implements",
                LocateRule.First(new StringRule("source"), " --|> ", new StringRule("destination")));
    }

    private static Stream<Node> modifyStructureDefinition(Node node) {
        final var retyped = node.is("record") ? node.retype("class") : node;

        final var maybeSupertype = retyped.findString("supertype");
        if (maybeSupertype.isPresent()) {
            final var supertype = maybeSupertype.get();
            final var name = retyped.findString("name")
                    .orElse("");

            return Stream.of(retyped,
                    new MapNode("implements").withString("source", name)
                            .withString("destination", supertype));
        }

        return Stream.of(retyped);
    }

    private static Rule createPlantUMLClassesRule() {
        return new OrRule(List.of(createPlantUMLClassRule("class"), createPlantUMLClassRule("interface")));
    }

    private static Rule createStructureDefinitionsRule() {
        return new OrRule(List.of(createStructureDefinitionRule("class"),
                createStructureDefinitionRule("interface"),
                createStructureDefinitionRule("record")));
    }

    private static Rule createStructureDefinitionRule(String type) {
        final Rule beforeType = new StringRule("before-type");

        final Rule name = new StringRule("name");
        final Rule withParams = new OrRule(List.of(LocateRule.First(name, "(", new StringRule("params")), name));
        final Rule afterType = new OrRule(List.of(LocateRule.Last(withParams,
                " implements ",
                new StringRule("supertype")), withParams));

        return new TypeRule(type, LocateRule.First(beforeType, type + " ", afterType));
    }

    private static Rule createPlantUMLClassRule(String type) {
        final var afterType = new StringRule("name");
        return new TypeRule(type, new PrefixRule(type + " ", afterType));
    }
}
