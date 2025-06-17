package magma;

import magma.app.divide.DivideState;
import magma.app.divide.MutableDivideState;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        final var joinedName = joined + "." + name;

        final var output = compileRoot(input, joinedName);
        return "class " + joinedName + "\n" + output;
    }

    private static String compileRoot(CharSequence input, String source) {
        return divide(input).stream()
                .map(createImportRule()::lex)
                .flatMap(Optional::stream)
                .toList()
                .stream()
                .map(node -> node.withString("source", source))
                .toList()
                .stream()
                .map(createDependencyRule()::generate)
                .flatMap(Optional::stream)
                .collect(Collectors.joining());
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

    private static Rule createImportRule() {
        return new StripRule(new PrefixRule("import ", new SuffixRule(new StringRule("destination"), ";")));
    }

    private static Rule createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}
