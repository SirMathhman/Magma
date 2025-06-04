package magma;

import magma.result.Err;
import magma.result.Ok;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import magma.option.Option;

public class GenerateDiagram {
    // Helper methods split to comply with SRP (Single Responsibility Principle)

    /**
     * Generates a PlantUML diagram and writes it to {@code output}. Instead of
     * throwing an exception, any I/O error is returned wrapped in an
     * {@link magma.option.Option}.
     */
    public static Option<IOException> writeDiagram(Path output) {
        Path src = Path.of("src/java/magma");
        var sources = Sources.read(src);
        if (sources.isErr()) {
            return Option.some(((Err<List<String>, IOException>) sources).error());
        }
        List<String> allSources = ((Ok<List<String>, IOException>) sources).value();
        Sources analysis = new Sources(allSources);
        List<String> classes = analysis.findClasses();

        var implementations = analysis.findImplementations();
        var sourceMap = analysis.mapSourcesByClass();

        StringBuilder content = new StringBuilder("@startuml\n");
        content.append(classesSection(classes, sourceMap));
        content.append(analysis.formatRelations(classes, implementations));
        content.append("@enduml\n");
        try {
            Files.writeString(output, content.toString());
            return Option.none();
        } catch (IOException e) {
            return Option.some(e);
        }
    }

    private static String classesSection(List<String> classes,
                                         java.util.Map<String, String> sourceMap) {
        StringBuilder builder = new StringBuilder();
        for (String name : classes) {
            String source = sourceMap.getOrDefault(name, "");
            String type = classType(name, source);
            builder.append(type).append(' ').append(name).append("\n");
        }
        return builder.toString();
    }

    private static String classType(String name, String source) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(class|interface|record)\\s+" + java.util.regex.Pattern.quote(name) + "\\b");
        java.util.regex.Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            String kind = matcher.group(1);
            if ("interface".equals(kind)) {
                return "interface";
            }
        }
        return "class";
    }

    /**
     * Creates a .ts file for every .java file under {@code javaRoot}. The
     * generated files mirror the directory structure under {@code tsRoot}.
     * Existing files are overwritten so that imports stay in sync with the
     * corresponding Java sources.
     */
    public static Option<IOException> writeTypeScriptStubs(Path javaRoot, Path tsRoot) {
        return TypeScriptStubs.write(javaRoot, tsRoot);
    }
}
