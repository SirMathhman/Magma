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
        content.append("@enduml\n");
        try {
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


    public static void main(String[] args) {
        writeDiagram(Path.of("diagram.puml"));
    }
}
