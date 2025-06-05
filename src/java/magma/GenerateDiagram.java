package magma;

import magma.option.Option;
import magma.option.Some;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateDiagram {
    // Helper methods split to comply with SRP (Single Responsibility Principle)

    /**
     * Generates a PlantUML diagram and writes it to {@code output}. Instead of
     * throwing an exception, any I/O error is returned wrapped in an
     * {@link Option}.
     */
    public static Option<IOException> writeDiagram(PathLike output) {
        PathLike src = JVMPath.of("src/java/magma");
        return Sources.read(src).match(allSources -> {
            Sources analysis = new Sources(allSources);
            List<String> classes = analysis.findClasses();

            var implementations = analysis.findImplementations();
            var sourceMap = analysis.mapSourcesByClass();

            String content = "@startuml\n" + "skinparam linetype ortho\n" +
                    classesSection(classes, sourceMap) +
                    analysis.formatRelations(classes, implementations) +
                    "@enduml\n";
            return output.writeString(content);
        }, Some::new);
    }

    private static String classesSection(List<String> classes,
                                         Map<String, String> sourceMap) {
        StringBuilder builder = new StringBuilder();
        for (String name : classes) {
            String source = sourceMap.getOrDefault(name, "");
            String type = classType(name, source);
            builder.append(type).append(' ').append(name).append("\n");
        }
        return builder.toString();
    }

    private static String classType(String name, String source) {
        Pattern pattern = Pattern.compile(
                "(class|interface|record)\\s+" + Pattern.quote(name) + "\\b");
        Matcher matcher = pattern.matcher(source);
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
