package magma;

import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;

import java.io.IOException;
import java.util.List;

public class GenerateDiagram {
    // Helper methods split to comply with SRP (Single Responsibility Principle)

    /**
     * Generates a PlantUML diagram and writes it to {@code output}. Instead of
     * throwing an exception, any I/O error is returned wrapped in an
     * {@link magma.option.Option}.
     */
    public static Option<IOException> writeDiagram(PathLike output) {
        PathLike src = JVMPath.of("src/java/magma");
        var sources = Sources.read(src);
        if (sources.isErr()) {
            return new Some<>(((Err<List<String>, IOException>) sources).error());
        }
        List<String> allSources = ((Ok<List<String>, IOException>) sources).value();
        Sources analysis = new Sources(allSources);
        List<String> classes = analysis.findClasses();

        var implementations = analysis.findImplementations();
        var sourceMap = analysis.mapSourcesByClass();

        StringBuilder content = new StringBuilder("@startuml\n");
        content.append("skinparam linetype ortho\n");
        content.append(classesSection(classes, sourceMap));
        content.append(analysis.formatRelations(classes, implementations));
        content.append("@enduml\n");
        return output.writeString(content.toString());
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

    // Previously exposed a stub generation helper here which delegated to
    // {@link TypeScriptStubs}. The method was removed so that this class is
    // solely responsible for PlantUML generation.
}
