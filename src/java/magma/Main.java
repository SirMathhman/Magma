package magma;

import magma.app.Lang;
import magma.app.Node;
import magma.app.node.MapNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var output = new StringBuilder();
            for (var source : sources)
                output.append(compileSource(source));

            Files.writeString(Paths.get(".", "diagram.puml"), "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSource(Path source) throws IOException {
        final var fileName = source.getFileName()
                .toString();

        final var extensionSeparator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, extensionSeparator);

        final var input = Files.readString(source);
        return Lang.createJavaRootRule()
                .lex(input)
                .flatMap(tree -> getString(tree, name))
                .orElse("");
    }

    private static Optional<String> getString(Node tree, String name) {
        final var children1 = transform(tree, name);
        return Lang.createPlantUMLRootRule()
                .generate(children1)
                .map(joined -> generate(name, joined));
    }

    private static String generate(String name, String joined) {
        return "class " + name + "\n" + joined;
    }

    private static Node transform(Node tree, String name) {
        final var list = tree.nodeLists()
                .find("children")
                .orElse(new ArrayList<>())
                .stream()
                .map(segment -> segment.strings()
                        .with("parent", name))
                .toList();

        return new MapNode().nodeLists()
                .with("children", list);
    }
}
