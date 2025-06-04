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
        Path src = Path.of("src/magma");
        Result<List<String>, IOException> sources = readSources(src);
        if (sources.isErr()) {
            return err(((Err<List<String>, IOException>) sources).error());
        }
        List<String> allSources = ((Ok<List<String>, IOException>) sources).value();
        List<String> classes = findClasses(allSources);
        StringBuilder content = new StringBuilder("@startuml\n");
        for (String name : classes) {
            content.append("class ").append(name).append("\n");
        }
        List<String[]> relations = findRelations(allSources);
        for (String[] rel : relations) {
            content.append(rel[0]).append(" --|> ")
                    .append(rel[1]).append("\n");
        }
        content.append("@enduml\n");
        try {
            Files.writeString(output, content.toString());
            return ok(null);
        } catch (IOException e) {
            return err(e);
        }
    }

    private static List<String> findClasses(List<String> sources) {
        Pattern pattern = Pattern.compile("^\\s*(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?(?:class|interface)\\s+(\\w+)", Pattern.MULTILINE);
        Set<String> unique = new LinkedHashSet<>();
        for (String src : sources) {
            Matcher matcher = pattern.matcher(src);
            while (matcher.find()) {
                unique.add(matcher.group(1));
            }
        }
        List<String> names = new ArrayList<>(unique);
        Collections.sort(names);
        return names;
    }

    private static List<String[]> findRelations(List<String> sources) {
        Pattern extendsPattern = Pattern.compile("(?:class|interface)\\s+(\\w+)\\s+extends\\s+([\\w\\s,]+)");
        Pattern implementsPattern = Pattern.compile("class\\s+(\\w+)(?:\\s+extends\\s+\\w+)?\\s+implements\\s+([\\w\\s,]+)");

        List<String[]> relations = new ArrayList<>();
        for (String src : sources) {
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

    private static Result<List<String>, IOException> readSources(Path directory) {
        List<Path> files;
        try (Stream<Path> stream = Files.walk(directory)) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            return err(e);
        }

        List<String> sources = new ArrayList<>();
        for (Path file : files) {
            try {
                sources.add(Files.readString(file));
            } catch (IOException e) {
                return err(e);
            }
        }
        return ok(sources);
    }

    public static void main(String[] args) {
        writeDiagram(Path.of("diagram.puml"));
    }
}
