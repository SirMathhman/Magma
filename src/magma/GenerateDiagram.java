package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static magma.Result.err;
import static magma.Result.ok;

public class GenerateDiagram {
    public static Optional<IOException> writeDiagram(Path output) {
        try {
            List<String> classes = findClasses(Path.of("src/magma"));
            StringBuilder content = new StringBuilder("@startuml\n");
            for (String name : classes) {
                content.append("class ").append(name).append("\n");
            }
            content.append("@enduml\n");
            Files.writeString(output, content.toString());
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    private static List<String> findClasses(Path directory) throws IOException {
        Pattern pattern = Pattern.compile("^\\s*(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?(?:class|interface)\\s+(\\w+)", Pattern.MULTILINE);
        List<Path> files;
        try (Stream<Path> stream = Files.walk(directory)) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .collect(Collectors.toList());
        }
        Set<String> unique = new LinkedHashSet<>();
        for (Path file : files) {
            String src = Files.readString(file);
            Matcher matcher = pattern.matcher(src);
            while (matcher.find()) {
                unique.add(matcher.group(1));
            }
        }
        List<String> names = new ArrayList<>(unique);
        Collections.sort(names);
        return names;
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
            return err(((Result.Err<String, IOException>) source).error());
        }
        String declaration = "class " + GenerateDiagram.class.getSimpleName();
        String src = ((Result.Ok<String, IOException>) source).value();
        return ok(src.contains(declaration));
    }

    public static void main(String[] args) {
        writeDiagram(Path.of("diagram.puml"));
    }
}
