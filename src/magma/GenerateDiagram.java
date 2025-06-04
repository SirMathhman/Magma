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

import magma.Relation;

import static magma.Result.err;
import static magma.Result.ok;

public class GenerateDiagram {
    // Helper methods split to comply with SRP (Single Responsibility Principle)
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
        appendClasses(content, classes);
        List<Relation> relations = findRelations(allSources, classes);
        appendRelations(content, relations);
        content.append("@enduml\n");
        try {
            Files.writeString(output, content.toString());
            return ok(null);
        } catch (IOException e) {
            return err(e);
        }
    }

    private static List<String> findClasses(List<String> sources) {
        // Matches a class or interface declaration and captures the name.
        // It allows optional visibility, static/final/sealed modifiers and
        // works across multiple lines.
        Pattern pattern = Pattern.compile(
                "^\\s*(?:public\\s+|protected\\s+|private\\s+)?" +
                "(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?" +
                "(?:class|interface)\\s+(\\w+)",
                Pattern.MULTILINE);
        Set<String> unique = new LinkedHashSet<>();
        for (String src : sources) {
            addClassesFromSource(unique, src, pattern);
        }
        List<String> names = new ArrayList<>(unique);
        Collections.sort(names);
        return names;
    }

    private static void addClassesFromSource(Set<String> unique, String src, Pattern pattern) {
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            unique.add(matcher.group(1));
        }
    }

    private static List<Relation> findRelations(List<String> sources, List<String> classes) {
        List<Relation> inheritance = findInheritanceRelations(sources);
        List<Relation> dependencies = findDependencyRelations(sources, classes, inheritance);
        Set<Relation> all = new LinkedHashSet<>();
        all.addAll(inheritance);
        all.addAll(dependencies);
        return new ArrayList<>(all);
    }

    private static List<Relation> findInheritanceRelations(List<String> sources) {
        // Matches "class Child extends Parent" or "interface Child extends Parent".
        // Captures the child name in group 1 and the comma separated parent list
        // (without generics) in group 2.
        Pattern extendsPattern = Pattern.compile(
                "(?:class|interface)\\s+(\\w+)\\s+extends\\s+([\\w\\s,<>]+)");

        // Matches class implementations such as
        // "class Example implements InterfaceA, InterfaceB". Group 1 is the
        // class name and group 2 contains the comma separated interfaces.
        Pattern implementsPattern = Pattern.compile(
                "class\\s+(\\w+)(?:\\s+extends\\s+\\w+)?\\s+implements\\s+([\\w\\s,<>]+)");

        List<Relation> relations = new ArrayList<>();
        for (String src : sources) {
            // Strip generic type information such as "List<String>" as it
            // complicates the inheritance regexes below.
            src = src.replaceAll("<[^>]*>", "");
            addInheritance(relations, src, extendsPattern);
            addInheritance(relations, src, implementsPattern);
        }
        return relations;
    }

    private static void addInheritance(List<Relation> relations, String src, Pattern pattern) {
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            String child = matcher.group(1);
            String parents = matcher.group(2);
            for (String parent : parents.split(",")) {
                // Remove any generic type parameters from the parent name
                // before adding the relation.
                parent = parent.replaceAll("<.*?>", "").trim();
                if (!parent.isEmpty()) {
                    relations.add(new Relation(child, "--|>", parent));
                }
            }
        }
    }

    private static List<Relation> findDependencyRelations(List<String> sources, List<String> classes, List<Relation> inheritance) {
        // Captures the name from any class or interface declaration. Used to
        // detect dependencies between classes found in the source.
        Pattern classPattern = Pattern.compile("(?:class|interface)\\s+(\\w+)");
        Set<String> inherited = new LinkedHashSet<>();
        for (Relation rel : inheritance) {
            inherited.add(rel.from() + "->" + rel.to());
        }

        List<Relation> relations = new ArrayList<>();
        for (String src : sources) {
            Matcher matcher = classPattern.matcher(src);
            if (!matcher.find()) {
                continue;
            }
            String name = matcher.group(1);
            for (String other : classes) {
                if (other.equals(name)) {
                    continue;
                }
                if (src.contains(other) && !inherited.contains(name + "->" + other)) {
                    relations.add(new Relation(name, "-->", other));
                }
            }
        }
        return relations;
    }

    private static void appendClasses(StringBuilder content, List<String> classes) {
        for (String name : classes) {
            content.append("class ").append(name).append("\n");
        }
    }

    private static void appendRelations(StringBuilder content, List<Relation> relations) {
        for (Relation rel : relations) {
            content.append(rel.from()).append(' ')
                    .append(rel.arrow()).append(' ')
                    .append(rel.to()).append("\n");
        }
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
