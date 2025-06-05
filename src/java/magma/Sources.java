package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import magma.PathLike;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Utility record wrapping a list of source files. Methods operate on the
 * provided sources instead of requiring them as parameters.
 */
public record Sources(List<String> list) {

    public static Result<List<String>, IOException> read(PathLike directory) {
        List<java.nio.file.Path> files;
        try (Stream<java.nio.file.Path> stream = Files.walk(directory.unwrap())) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            return new Err<>(e);
        }

        List<String> sources = new ArrayList<>();
        for (java.nio.file.Path file : files) {
            try {
                sources.add(Files.readString(file));
            } catch (IOException e) {
                return new Err<>(e);
            }
        }
        return new Ok<>(sources);
    }
    public List<String> findClasses() {
        Pattern pattern = Pattern.compile(
                "^\\s*(?:public\\s+|protected\\s+|private\\s+)?" +
                "(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?" +
                "(?:class|interface|record)\\s+(\\w+)",
                Pattern.MULTILINE);
        Set<String> unique = new LinkedHashSet<>();
        for (String src : list) {
            unique.addAll(classesFromSource(src, pattern));
        }
        List<String> names = new ArrayList<>(unique);
        Collections.sort(names);
        return names;
    }

    public Map<String, List<String>> findImplementations() {
        Pattern implementsPattern = Pattern.compile(
                "(?:public\\s+|protected\\s+|private\\s+)?"
                        + "(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?"
                        + "(?:class|record)\\s+(\\w+)(?:\\([^)]*\\))?"
                        + "(?:\\s+extends\\s+\\w+)?\\s+implements\\s+([\\w\\s,<>]+)");
        Map<String, List<String>> map = new java.util.HashMap<>();
        for (String src : list) {
            map.putAll(implementationsForSource(src, implementsPattern));
        }
        return map;
    }

    public List<Relation> findInheritanceRelations() {
        Pattern extendsPattern = Pattern.compile(
                "(?:public\\s+|protected\\s+|private\\s+)?"
                        + "(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?"
                        + "(?:class|interface|record)\\s+(\\w+)(?:\\([^)]*\\))?"
                        + "\\s+extends\\s+([\\w\\s,<>]+)");
        Pattern implementsPattern = Pattern.compile(
                "(?:public\\s+|protected\\s+|private\\s+)?"
                        + "(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?"
                        + "(?:class|record)\\s+(\\w+)(?:\\([^)]*\\))?"
                        + "(?:\\s+extends\\s+\\w+)?\\s+implements\\s+([\\w\\s,<>]+)");
        List<Relation> relations = new ArrayList<>();
        for (String src : list) {
            src = src.replaceAll("<[^>]*>", "");
            src = stripComments(src);
            relations.addAll(inheritanceFromSource(src, extendsPattern));
            relations.addAll(inheritanceFromSource(src, implementsPattern));
        }
        return relations;
    }

    public Map<String, String> mapSourcesByClass() {
        Map<String, String> map = new java.util.HashMap<>();
        Pattern classPattern = Pattern.compile("(?:class|interface|record)\\s+(\\w+)");
        for (String src : list) {
            String stripped = stripComments(src);
            Matcher matcher = classPattern.matcher(stripped);
            if (matcher.find()) {
                map.put(matcher.group(1), stripped);
            }
        }
        return map;
    }

    public List<Relation> findDependencyRelations(List<String> classes,
                                                  List<Relation> inheritance,
                                                  Map<String, List<String>> implementations) {
        Pattern classPattern = Pattern.compile("(?:class|interface|record)\\s+(\\w+)");
        Map<String, String> sourceMap = mapSourcesByClass();
        Set<String> inherited = toInheritedSet(inheritance);
        List<Relation> relations = new ArrayList<>();
        for (String src : list) {
            relations.addAll(dependenciesForSource(src, classPattern, classes,
                    inherited, sourceMap, implementations));
        }
        return relations;
    }

    public List<Relation> findRelations(List<String> classes,
                                        Map<String, List<String>> implementations) {
        List<Relation> inheritance = findInheritanceRelations();
        List<Relation> dependencies = findDependencyRelations(classes, inheritance, implementations);
        Set<Relation> all = new LinkedHashSet<>();
        all.addAll(inheritance);
        all.addAll(dependencies);
        return new ArrayList<>(all);
    }

    private static Set<String> classesFromSource(String src, Pattern pattern) {
        Set<String> result = new LinkedHashSet<>();
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    private static List<Relation> inheritanceFromSource(String src, Pattern pattern) {
        List<Relation> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            String child = matcher.group(1);
            String parents = matcher.group(2);
            result.addAll(parentRelations(child, parents));
        }
        return result;
    }

    private static List<Relation> parentRelations(String child, String parents) {
        List<Relation> relations = new ArrayList<>();
        for (String parent : parents.split(",")) {
            parent = parent.replaceAll("<.*?>", "").trim();
            if (!parent.isEmpty()) {
                relations.add(new Relation(child, "--|>", parent));
            }
        }
        return relations;
    }

    private static Map<String, List<String>> implementationsForSource(String src, Pattern pattern) {
        src = src.replaceAll("<[^>]*>", "");
        src = stripComments(src);
        src = stripStrings(src);
        Matcher matcher = pattern.matcher(src);
        Map<String, List<String>> map = new java.util.HashMap<>();
        while (matcher.find()) {
            String child = matcher.group(1);
            String parents = matcher.group(2);
            map.put(child, parseInterfaces(parents));
        }
        return map;
    }

    private static List<String> parseInterfaces(String parents) {
        List<String> interfaces = new ArrayList<>();
        for (String parent : parents.split(",")) {
            parent = parent.replaceAll("<.*?>", "").trim();
            if (!parent.isEmpty()) {
                interfaces.add(parent);
            }
        }
        return interfaces;
    }

    private static String stripComments(String src) {
        src = src.replaceAll("(?s)/\\*.*?\\*/", "");
        src = src.replaceAll("//.*", "");
        return src;
    }

    private static String stripStrings(String src) {
        return src.replaceAll("\"(?:\\\\.|[^\"\\\\])*\"", "");
    }

    private static Set<String> toInheritedSet(List<Relation> inheritance) {
        Set<String> set = new LinkedHashSet<>();
        for (Relation rel : inheritance) {
            set.add(rel.from() + "->" + rel.to());
        }
        return set;
    }

    private static Map<String, List<String>> invertImplementations(Map<String, List<String>> implementations) {
        Map<String, List<String>> map = new java.util.LinkedHashMap<>();
        for (var entry : implementations.entrySet()) {
            String child = entry.getKey();
            for (String iface : entry.getValue()) {
                map.computeIfAbsent(iface, k -> new java.util.ArrayList<>()).add(child);
            }
        }
        return map;
    }

    private static boolean containsReference(String source, List<String> names) {
        for (String name : names) {
            Pattern word = Pattern.compile("\\b" + Pattern.quote(name) + "\\b");
            if (word.matcher(source).find()) {
                return true;
            }
        }
        return false;
    }

    private static List<Relation> dependenciesForSource(String src,
                                                         Pattern classPattern,
                                                         List<String> classes,
                                                         Set<String> inherited,
                                                         Map<String, String> sourceMap,
                                                         Map<String, List<String>> implementations) {
        List<Relation> relations = new ArrayList<>();
        src = stripComments(src);
        src = stripStrings(src);
        Matcher matcher = classPattern.matcher(src);
        if (!matcher.find()) {
            return relations;
        }
        String name = matcher.group(1);
        Map<String, List<String>> byInterface = invertImplementations(implementations);
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
            List<String> otherIfaces = implementations.get(other);
            if (otherIfaces != null && otherIfaces.contains(name)) {
                continue;
            }
            List<String> impls = byInterface.get(other);
            if (impls != null && containsReference(src, impls)) {
                continue;
            }
            relations.add(new Relation(name, "-->", other));
        }
        return relations;
    }

    public String formatRelations(List<String> classes,
                                  Map<String, List<String>> implementations) {
        StringBuilder builder = new StringBuilder();
        for (Relation rel : findRelations(classes, implementations)) {
            builder.append(rel.from()).append(' ')
                   .append(rel.arrow()).append(' ')
                   .append(rel.to()).append("\n");
        }
        return builder.toString();
    }
}
