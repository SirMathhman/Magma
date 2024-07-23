package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {
            var source = resolve("java");
            var input = Files.readString(source);
            var output = compile(input);
            var target = resolve("mgs");
            Files.writeString(target, output);
        } catch (IOException | CompileException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String root) throws CompileException {
        var segments = Splitter.split(root);
        var compiledSegments = compileRootMembers(segments, 0);
        return generateBlock(compiledSegments, 0);
    }

    private static String generateBlock(List<String> compiledSegments, int depth) {
        var builder = new StringBuilder();
        for (int i = 0; i < compiledSegments.size(); i++) {
            var segment = compiledSegments.get(i);
            var prefix = (i == 0 && depth == 0) ? "" : "\n";
            builder.append(prefix).append(segment);
        }
        return builder.toString();
    }

    private static List<String> compileRootMembers(List<String> segments, int depth) throws CompileException {
        var compiledSegments = new ArrayList<String>();
        for (String segment : segments) {
            var stripped = segment.strip();
            if(stripped.isEmpty()) continue;

            var compiled = compileRootMember(stripped, depth + 1);
            if (!compiled.isEmpty()) compiledSegments.add(compiled);
        }
        return compiledSegments;
    }

    private static String compileRootMember(String segment, int depth) throws CompileException {
        if (segment.startsWith("package ")) return "";
        if (segment.startsWith("import ")) return segment;

        return compileClass(segment, depth)
                .orElseGet(() -> new Err<>(new CompileException("Unknown root member", segment)))
                .unwrap();
    }

    private static Optional<Result<String, CompileException>> compileClass(String rootMember, int depth) {
        var classIndex = rootMember.indexOf("class ");
        if (classIndex == -1) return Optional.empty();

        var oldModifiers = rootMember.substring(0, classIndex);
        var newModifiers = oldModifiers.equals("public ") ? "export " : "";

        var afterClass = rootMember.substring(classIndex + "class ".length());
        var contentStart = afterClass.indexOf('{');
        if (contentStart == -1) return Optional.empty();

        var name = afterClass.substring(0, contentStart).strip();
        var afterContentStart = afterClass.substring(contentStart + 1).strip();
        if (!afterContentStart.endsWith("}")) return Optional.empty();

        var content = afterContentStart.substring(0, afterContentStart.length() - 1);

        var oldClassMembers = Splitter.split(content);
        Result<JavaList<String>, CompileException> newClassMembers = new Ok<>(new JavaList<>());
        for (String oldClassMember : oldClassMembers) {
            var stripped = oldClassMember.strip();
            if(stripped.isEmpty()) continue;

            newClassMembers = newClassMembers
                    .and(() -> compileClassMember(stripped, depth + 1))
                    .mapValue(tuple -> tuple.left().add(tuple.right()));
        }

        return Optional.of(newClassMembers.mapValue(value -> renderFunction(name, newModifiers + "class ", value, depth)));
    }

    private static String renderFunction(String name, String newModifiers, JavaList<String> content, int depth) {
        return newModifiers + "def " + name + "() => {" + generateBlock(content.list(), depth) + "}";
    }

    private static Result<String, CompileException> compileClassMember(String classMember, int depth) {
        return compileMethod(classMember, depth)
                .orElseGet(() -> new Err<>(new CompileException("Invalid class member", classMember)));
    }

    private static Optional<Result<String, CompileException>> compileMethod(String classMember, int depth) {
        var separator = classMember.indexOf('(');
        if (separator == -1) return Optional.empty();

        var before = classMember.substring(0, separator).strip();
        var space = before.lastIndexOf(" ");
        var name = before.substring(space + 1).strip();
        return Optional.of(new Ok<>(renderFunction(name, "", new JavaList<>(), depth)));
    }

    private static Path resolve(String extension) {
        return Paths.get(".", "src", "magma", "Main." + extension);
    }

}
