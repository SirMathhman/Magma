package magma;

import magma.app.MaybeNodeList;
import magma.app.Node;
import magma.app.Rule;
import magma.app.State;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;
import magma.app.maybe.string.PresentString;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();
            final var output = compileSources(sources);
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSources(List<Path> sources) throws IOException {
        final var output = new StringBuilder();
        for (var source : sources)
            output.append(compileSource(source));
        return output.toString();
    }

    private static String compileSource(Path source) throws IOException {
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        final var input = Files.readString(source);
        final var result = compileRoot(input, name);

        return "class " + name + "\n" + result;
    }

    private static String compileRoot(String input, String name) {
        return lex(input, createImportRule()).transform(children -> transform(name, children)).generate(Main::generate).orElse("");
    }

    private static MaybeString generate(List<Node> children) {
        return children.stream().map(node -> createDependencyRule().generate(node)).reduce(new PresentString(""), MaybeString::appendMaybe, (_, next) -> next);
    }

    private static List<Node> transform(String name, List<Node> list) {
        return list.stream().map(node -> node.withString("source", name)).toList();
    }

    private static MaybeNodeList lex(String input, Rule<Node, MaybeNode, MaybeString> rule) {
        return divide(input).stream().map(rule::lex).reduce(new PresentNodeList(), MaybeNodeList::add, (_, next) -> next);
    }

    private static Rule<Node, MaybeNode, MaybeString> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    private static Rule<Node, MaybeNode, MaybeString> createImportRule() {
        return new StripRule<>(new PrefixRule("import ", new SuffixRule<>(new InfixRule<>(new StringRule("parent"), ".", new StringRule("destination")), ";")));
    }

    private static List<String> divide(String input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments();
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }
}
