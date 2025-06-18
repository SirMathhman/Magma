package magma;

import magma.app.LocateRule;
import magma.app.MutableState;
import magma.app.OrRule;
import magma.app.PrefixRule;
import magma.app.Rule;
import magma.app.State;
import magma.app.StringRule;
import magma.app.SuffixRule;
import magma.app.TypeRule;
import magma.app.node.MapNode;
import magma.app.node.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var output = compileAll(sources);
            Files.writeString(Paths.get(".", "diagram.puml"),
                    "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileAll(Iterable<Path> sources) throws IOException {
        final StringBuilder output = new StringBuilder();
        for (var source : sources) {
            final var input = Files.readString(source);
            final var segments = divide(input);

            final var fileName = source.getFileName()
                    .toString();
            final var separator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, separator);

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
                .map(Main::modifyStructureDefinition)
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
