package magma;

import magma.app.Lang;
import magma.app.node.properties.PropertiesCompoundNode;
import magma.app.node.CompoundNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).collect(Collectors.toSet());

            final var output = compileSources(sources);
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSources(Set<Path> sources) throws IOException {
        final var output1 = new StringBuilder();
        for (var source : sources)
            output1.append(compileSource(source));
        return "@startuml\nskinparam linetype ortho\n" + output1 + "@enduml";
    }

    private static String compileSource(Path source) throws IOException {
        final var input = Files.readString(source);
        final var fileName = source.getFileName().toString();
        final var name = fileName.substring(0, fileName.lastIndexOf("."));
        return "class " + name + "\n" + compileInput(input, name);
    }

    private static String compileInput(String input, String name) {
        return Lang.createJavaRootRule().lex(input).findValue().flatMap(root -> {
            final var parsed = transform(name, root);
            return Lang.createPlantRootRule().generate(parsed).findValue();
        }).orElse("");
    }

    private static CompoundNode transform(String name, CompoundNode root) {
        final var children = root.nodeLists().find("children").orElse(new ArrayList<>());
        CompoundNode node = new PropertiesCompoundNode();
        List<CompoundNode> values = children.stream().map(child -> child.strings().with("source", name)).toList();
        return node.nodeLists().with("children", values);
    }
}