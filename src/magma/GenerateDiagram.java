package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static magma.Result.err;
import static magma.Result.ok;

public class GenerateDiagram {
    /**
     * Generates a PlantUML diagram and writes it to {@code output}. Instead of
     * throwing an exception, any I/O error is returned wrapped in an
     * {@code Optional}.
     */
    public static Result<Void, IOException> writeDiagram(Path output) {
        Result<List<String>, IOException> result = findClasses(Path.of("src/magma"));
        if (result.isErr()) {
            return err(((Err<List<String>, IOException>) result).error());
        }
        List<String> classes = ((Ok<List<String>, IOException>) result).value();
        StringBuilder content = new StringBuilder("@startuml\n");
        for (String name : classes) {
            content.append("class ").append(name).append("\n");
        }
        try {
            Path src = Path.of("src/magma");
            List<String[]> relations = findRelations(src);
            for (String[] rel : relations) {
                content.append(rel[0]).append(" --|> ")
                        .append(rel[1]).append("\n");
            }
            content.append("@enduml\n");
            Files.writeString(output, content.toString());
            return ok(null);
        } catch (IOException e) {
            return err(e);
        }
    }

    private static Result<List<String>, IOException> findClasses(Path directory) {
        Pattern pattern = Pattern.compile("^\\s*(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?(?:class|interface)\\s+(\\w+)", Pattern.MULTILINE);
        List<Path> files;
        try (Stream<Path> stream = Files.walk(directory)) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            return err(e);
        }
        Set<String> unique = new LinkedHashSet<>();
        for (Path file : files) {
            String src;
            try {
                src = Files.readString(file);
            } catch (IOException e) {
                return err(e);
            }
            Matcher matcher = pattern.matcher(src);
            while (matcher.find()) {
                unique.add(matcher.group(1));
            }
        }
        List<String> names = new ArrayList<>(unique);
        Collections.sort(names);
        return ok(names);
    }

    private static List<String[]> findRelations(Path directory) throws IOException {
        Pattern extendsPattern = Pattern.compile("(?:class|interface)\\s+(\\w+)\\s+extends\\s+([\\w\\s,]+)");
        Pattern implementsPattern = Pattern.compile("class\\s+(\\w+)(?:\\s+extends\\s+\\w+)?\\s+implements\\s+([\\w\\s,]+)");

        List<Path> files;
        try (Stream<Path> stream = Files.walk(directory)) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        }

        List<String[]> relations = new ArrayList<>();
        for (Path file : files) {
            String src = Files.readString(file);
            src = src.replaceAll("<[^>]*>", "");

            Matcher matcher = extendsPattern.matcher(src);
            while (matcher.find()) {
                String child = matcher.group(1);
                String parents = matcher.group(2);
                for (String parent : parents.split(",")) {
                    parent = parent.replaceAll("<.*?>", "").trim();
                    if (!parent.isEmpty()) {
                        relations.add(new String[]{child, parent});
                    }
                }
            }

            matcher = implementsPattern.matcher(src);
            while (matcher.find()) {
                String child = matcher.group(1);
                String parents = matcher.group(2);
                for (String parent : parents.split(",")) {
                    parent = parent.replaceAll("<.*?>", "").trim();
                    if (!parent.isEmpty()) {
                        relations.add(new String[]{child, parent});
                    }
                }
            }
        }
        return relations;
    }

    /**
     * Reads the source code of this class.
     *
     * @return the contents of this class's source file
     */
    public static Result<String, IOException> readSelf() {
        try {
            String fileName = GenerateDiagram.class.getSimpleName() + ".java";
            Path self = Path.of("src/magma", fileName);
            return ok(Files.readString(self));
        } catch (IOException e) {
            return err(e);
        }
    }

    /**
     * Determines if the source code contains its own class declaration.
     */
    public static Result<Boolean, IOException> hasClassDeclaration() {
        Result<String, IOException> source = readSelf();
        if (source.isErr()) {
            return err(((Err<String, IOException>) source).error());
        }
        String declaration = "class " + GenerateDiagram.class.getSimpleName();
        String src = ((Ok<String, IOException>) source).value();
        return ok(src.contains(declaration));
    }

    public static void main(String[] args) {
        writeDiagram(Path.of("diagram.puml"));
    }
}
