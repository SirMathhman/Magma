package magma;

import magma.app.divide.DivideState;
import magma.app.divide.MutableDivideState;
import magma.app.node.Node;
import magma.app.rule.PrefixRule;
import magma.app.rule.StringRule;
import magma.app.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (var files = Files.walk(sourceDirectory)) {
            final var sources = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith("java"))
                    .toList();

            final var stringBuilder = new StringBuilder();
            for (var source : sources)
                stringBuilder.append(compileSource(sourceDirectory, source));

            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" + stringBuilder + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSource(Path sourceDirectory, Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source)
                .getParent();

        final Collection<String> namespace = new ArrayList<>();
        for (var i = 0; i < relativeParent.getNameCount(); i++)
            namespace.add(relativeParent.getName(i)
                    .toString());

        final var fileName = source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        final var joined = String.join(".", namespace);
        final var input = Files.readString(source);
        final var segments = divide(input);
        final var joinedName = joined + "." + name;
        final var output = compileSegments(segments, joinedName);
        return "class " + joinedName + "\n" + output;
    }

    private static List<String> divide(CharSequence input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static DivideState fold(DivideState divideState, char c) {
        final var appended = divideState.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    private static StringBuilder compileSegments(Iterable<String> segments, String name) {
        final var output = new StringBuilder();
        for (var segment : segments)
            compileImport(segment, name).ifPresent(output::append);
        return output;
    }

    private static Optional<String> compileImport(String segment, String name) {
        final var stripped = segment.strip();
        return new PrefixRule("import ", new SuffixRule(new StringRule("destination"), ";")).lex(stripped)
                .map(node -> node.withString("source", name))
                .flatMap(Main::generate);
    }

    private static Optional<String> generate(Node node) {
        return Optional.of(node.findString("source")
                .orElse("") + " --> " + node.findString("destination")
                .orElse("") + "\n");
    }
}
