package magma;

import magma.api.Tuple;
import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.Node;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.SplitRule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        final var root = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(root)) {
            final Collector<Path, ?, Set<Path>> setCollector = Collectors.toSet();
            final var files = stream.filter(Files::isRegularFile).filter(Main::isJavaFile).collect(setCollector);

            final var outputRootSegments = Main.runWithSources(files);
            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Main.LINE_SEPARATOR, outputRootSegments);
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static boolean isJavaFile(final Path file) {
        final var asString = file.toString();
        return asString.endsWith(".java");
    }

    private static Collection<String> runWithSources(final Iterable<Path> files) throws IOException {
        final var pre = List.of("@startuml", "skinparam linetype ortho");
        final Collection<String> outputRootSegments = new ArrayList<>(pre);
        for (final var source : files) {
            final var input = Files.readString(source);

            final var fileName = source.getFileName().toString();
            final var separator = fileName.lastIndexOf('.');
            final var parent = fileName.substring(0, separator);

            final var output = Main.compile(input, parent);
            outputRootSegments.add("class " + parent);
            outputRootSegments.addAll(output);
        }

        outputRootSegments.add("@enduml");
        return outputRootSegments;
    }

    private static List<String> compile(final CharSequence input, final String parent) {
        final var segments = Main.divide(input).toList();
        final List<String> output = new ArrayList<>();
        for (final var segment : segments) Main.compileRootSegment(segment, parent).ifPresent(output::add);
        return output;
    }

    private static Optional<String> compileRootSegment(final String input, final String parent) {
        return Main.createImportRule()
                   .lex(input)
                   .map(child1 -> Main.modifyImport(parent, child1))
                   .map(node -> Main.createDependencyRule().generate(node).orElse(""))
                   .or(() -> Main.compileStructure(input));
    }

    private static Rule createImportRule() {
        final Rule child = new StringRule("child");
        return new StripRule(
                new SuffixRule(new PrefixRule("import ", SplitRule.Last(new StringRule("discard"), ".", child)), ";"));
    }

    private static Optional<String> compileStructure(final String input) {
        final var strip = input.strip();
        final var stripLength = strip.length();
        if (strip.isEmpty() || '}' != strip.charAt(stripLength - 1)) return Optional.empty();
        final var suffixLength = "}".length();
        final var substring = strip.substring(0, stripLength - suffixLength);

        final var contentStart = substring.indexOf('{');
        if (0 > contentStart) return Optional.empty();
        final var header = substring.substring(0, contentStart);
        return Main.compileStructureHeader(header);
    }

    private static Optional<String> compileStructureHeader(final String header) {
        return Main.createStructureHeaderRule()
                   .lex(header)
                   .map(Main::modifyStructureHeader)
                   .flatMap(result -> Main.createPlantStructureRule().generate(result));
    }

    private static Rule createPlantStructureRule() {
        return new OrRule(
                List.of(Main.createTypedPlantStructureRule("class"), Main.createTypedPlantStructureRule("interface")));
    }

    private static Rule createStructureHeaderRule() {
        return new OrRule(List.of(Main.createClassHeaderRule("class"), Main.createClassHeaderRule("interface"),
                                  Main.createRecordHeaderRule()));
    }

    private static Node modifyStructureHeader(final Node header) {
        if (header.is("record")) {
            final var content = header.findString("name").orElse("") + " " + header.findString("more").orElse("");
            return header.retype("class").withString("content", content);
        }

        return header;
    }

    private static Rule createRecordHeaderRule() {
        final Rule modifiers = new StringRule("modifiers");
        final Rule name = new StringRule("name");
        final Rule params = new StringRule("params");
        final var withParams = SplitRule.First(name, "(", params);
        final var afterKeyword = SplitRule.First(withParams, ")", new StringRule("more"));
        return SplitRule.First(modifiers, "record ", afterKeyword);
    }

    private static Rule createClassHeaderRule(final String type) {
        return new TypeRule(type, SplitRule.Last(new StringRule("discard"), type + " ", new StringRule("content")));
    }

    private static Rule createTypedPlantStructureRule(final String type) {
        return new TypeRule(type, new PrefixRule(type + " ", new StringRule("content")));
    }

    private static Node modifyImport(final String parent, final Node child1) {
        return child1.withString("parent", parent);
    }

    private static Rule createDependencyRule() {
        return SplitRule.First(new StringRule("parent"), " <-- ", new StringRule("child"));
    }

    private static Stream<String> divide(final CharSequence input) {
        var current = new Tuple<>(true, (DivideState) new MutableDivideState(input));
        while (current.left()) {
            final var right = current.right();
            current = Main.fold(right);
        }

        return current.right().advance().stream();
    }

    private static Tuple<Boolean, DivideState> fold(final DivideState state) {
        final var maybeNextTuple = state.pop();
        if (maybeNextTuple.isEmpty()) return new Tuple<>(false, state);

        final var nextTuple = maybeNextTuple.get();
        final var nextState = nextTuple.left();
        final var next = nextTuple.right();

        final var folded = Main.fold(nextState, next);
        return new Tuple<>(true, folded);
    }

    private static DivideState fold(final DivideState current, final char next) {
        final var appended = current.append(next);
        if (';' == next && appended.isLevel()) return appended.advance();
        if ('{' == next) return appended.enter();
        if ('}' == next) return appended.exit();
        return appended;
    }
}
