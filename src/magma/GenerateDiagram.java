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
import magma.Unit;

public class GenerateDiagram {
    // Helper methods split to comply with SRP (Single Responsibility Principle)
    /**
     * Generates a PlantUML diagram and writes it to {@code output}. Instead of
     * throwing an exception, any I/O error is returned wrapped in an
     * {@code Optional}.
     */
    public static Result<Unit, IOException> writeDiagram(Path output) {
        Path src = Path.of("src/magma");
        Result<List<String>, IOException> sources = readSources(src);
        if (sources.isErr()) {
            return new Err<>(((Err<List<String>, IOException>) sources).error());
        }
        List<String> allSources = ((Ok<List<String>, IOException>) sources).value();
        List<String> classes = findClasses(allSources);
        var implementations = findImplementations(allSources);
        StringBuilder content = new StringBuilder("@startuml\n");
        appendClasses(content, classes);
        List<Relation> relations = findRelations(allSources, classes, implementations);
        appendRelations(content, relations);
        content.append("@enduml\n");
        try {
            Files.writeString(output, content.toString());
            return new Ok<>(Unit.INSTANCE);
        } catch (IOException e) {
            return new Err<>(e);
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

    private static List<Relation> findRelations(List<String> sources,
                                               List<String> classes,
                                               java.util.Map<String, java.util.List<String>> implementations) {
        List<Relation> inheritance = findInheritanceRelations(sources);
        List<Relation> dependencies =
                findDependencyRelations(sources, classes, inheritance, implementations);
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
            src = stripComments(src);
            addInheritance(relations, src, extendsPattern);
            addInheritance(relations, src, implementsPattern);
        }
        return relations;
    }

    private static String stripComments(String src) {
        src = src.replaceAll("(?s)/\\*.*?\\*/", "");
        src = src.replaceAll("//.*", "");
        return src;
    }

    private static void addInheritance(List<Relation> relations, String src, Pattern pattern) {
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            String child = matcher.group(1);
            String parents = matcher.group(2);
            addParentRelations(child, parents, relations);
        }
    }

    private static void addParentRelations(String child,
                                           String parents,
                                           List<Relation> relations) {
        for (String parent : parents.split(",")) {
            parent = parent.replaceAll("<.*?>", "").trim();
            if (!parent.isEmpty()) {
                relations.add(new Relation(child, "--|>", parent));
            }
        }
    }

    private static java.util.Map<String, java.util.List<String>> findImplementations(List<String> sources) {
        Pattern implementsPattern = Pattern.compile(
                "class\\s+(\\w+)(?:\\s+extends\\s+\\w+)?\\s+implements\\s+([\\w\\s,<>]+)");
        java.util.Map<String, java.util.List<String>> map = new java.util.HashMap<>();
        for (String src : sources) {
            addImplementationsForSource(src, implementsPattern, map);
        }
        return map;
    }

    private static void addImplementationsForSource(String src,
                                                    Pattern pattern,
                                                    java.util.Map<String, java.util.List<String>> map) {
        src = src.replaceAll("<[^>]*>", "");
        src = stripComments(src);
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            String child = matcher.group(1);
            String parents = matcher.group(2);
            map.put(child, parseInterfaces(parents));
        }
    }

    private static java.util.List<String> parseInterfaces(String parents) {
        java.util.List<String> interfaces = new java.util.ArrayList<>();
        for (String parent : parents.split(",")) {
            parent = parent.replaceAll("<.*?>", "").trim();
            if (!parent.isEmpty()) {
                interfaces.add(parent);
            }
        }
        return interfaces;
    }

    private static java.util.Map<String, String> mapSourcesByClass(List<String> sources) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        Pattern classPattern = Pattern.compile("(?:class|interface)\\s+(\\w+)");
        for (String src : sources) {
            String stripped = stripComments(src);
            Matcher matcher = classPattern.matcher(stripped);
            if (matcher.find()) {
                map.put(matcher.group(1), stripped);
            }
        }
        return map;
    }

    private static java.util.Set<String> toInheritedSet(List<Relation> inheritance) {
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        for (Relation rel : inheritance) {
            set.add(rel.from() + "->" + rel.to());
        }
        return set;
    }

    private static boolean omitDependency(java.util.Optional<String> source,
                                          String dependency,
                                          java.util.Map<String, java.util.List<String>> implementations) {
        if (source.isEmpty()) {
            return false;
        }
        java.util.List<String> interfaces =
                implementations.getOrDefault(dependency, java.util.Collections.emptyList());
        return !interfaces.isEmpty() &&
                containsInterfaceReference(source.get(), interfaces);
    }

    private static boolean containsInterfaceReference(String source, java.util.List<String> interfaces) {
        for (String iface : interfaces) {
            Pattern word = Pattern.compile("\\b" + Pattern.quote(iface) + "\\b");
            if (word.matcher(source).find()) {
                return true;
            }
        }
        return false;
    }

    private static List<Relation> findDependencyRelations(List<String> sources,
                                                         List<String> classes,
                                                         List<Relation> inheritance,
                                                         java.util.Map<String, java.util.List<String>> implementations) {
        Pattern classPattern = Pattern.compile("(?:class|interface)\\s+(\\w+)");

        java.util.Map<String, String> sourceMap = mapSourcesByClass(sources);

        Set<String> inherited = toInheritedSet(inheritance);

        List<Relation> relations = new ArrayList<>();
        for (String src : sources) {
            processDependenciesForSource(src, classPattern, classes, inherited, sourceMap, implementations, relations);
        }
        return relations;
    }

    private static void processDependenciesForSource(String src,
                                                     Pattern classPattern,
                                                     List<String> classes,
                                                     Set<String> inherited,
                                                     java.util.Map<String, String> sourceMap,
                                                     java.util.Map<String, java.util.List<String>> implementations,
                                                     List<Relation> relations) {
        src = stripComments(src);
        Matcher matcher = classPattern.matcher(src);
        if (!matcher.find()) {
            return;
        }
        String name = matcher.group(1);
        for (String other : classes) {
            if (other.equals(name)) {
                continue;
            }
            Pattern word = Pattern.compile("\\b" + Pattern.quote(other) + "\\b");
            if (!word.matcher(src).find()) {
                continue;
            }
            if (inherited.contains(name + "->" + other)) {
                continue;
            }
            if (omitDependency(java.util.Optional.ofNullable(sourceMap.get(name)),
                              other, implementations)) {
                continue;
            }
            relations.add(new Relation(name, "-->", other));
        }
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
            return new Err<>(e);
        }

        List<String> sources = new ArrayList<>();
        for (Path file : files) {
            try {
                sources.add(Files.readString(file));
            } catch (IOException e) {
                return new Err<>(e);
            }
        }
        return new Ok<>(sources);
    }

    public static void main(String[] args) {
        writeDiagram(Path.of("diagram.puml"));
    }
}
