package magma;

import magma.app.Generated;
import magma.app.Node;
import magma.app.State;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.StringRule;
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
        final var segments = divide(input);
        var output = new StringBuilder();
        for (var segment : segments)
            output = compileRootSegment(name, segment).appendTo(output);

        return output.toString();
    }

    private static Generated compileRootSegment(String name, String input) {
        final var strip = input.strip();
        final InfixRule destination = new InfixRule(".", new StringRule("destination"));
        final var generated = new PrefixRule("import ", new SuffixRule(destination, ";")).lex(strip);
        final var source = generated.withString("source", name);
        return source.generate(Main::generateDependency);
    }

    private static String generateDependency(Node node) {
        return node.findString("source").orElse("") + " --> " + node.findString("destination").orElse("") + "\n";
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
