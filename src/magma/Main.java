package magma;

import magma.lang.MagmaLang;
import magma.lang.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        var compiledSegments = compileRootMembers(segments);
        return MagmaLang.generateBlock(compiledSegments, 0);
    }

    private static List<String> compileRootMembers(List<String> segments) throws CompileException {
        var compiledSegments = new ArrayList<String>();
        for (String segment : segments) {
            var stripped = segment.strip();
            if (stripped.isEmpty()) continue;

            var compiled = compileRootMember(stripped);
            if (!compiled.isEmpty()) compiledSegments.add(compiled);
        }
        return compiledSegments;
    }

    private static String compileRootMember(String segment) throws CompileException {
        if (segment.startsWith("package ")) return "";
        if (segment.startsWith("import ")) return segment;

        return compileClass(segment)
                .orElseGet(() -> new Err<>(new CompileException("Unknown root member", segment)))
                .unwrap();
    }

    private static Optional<Result<String, CompileException>> compileClass(String rootMember) {
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
        var newClassMembers = compileClassMembers(oldClassMembers);
        return Optional.of(newClassMembers.mapValue(value -> MagmaLang.generateFunction(new Node(
                Map.of(Node.DEPTH, 1),
                Map.of(Node.MODIFIERS, newModifiers + "class ", Node.NAME, name, Node.PARAMS, ""),
                Map.of(Node.CONTENT, value)
        ))));
    }

    private static Result<JavaList<String>, CompileException> compileClassMembers(List<String> oldClassMembers) {
        Result<JavaList<String>, CompileException> newClassMembers = new Ok<>(new JavaList<>());
        for (String oldClassMember : oldClassMembers) {
            var stripped = oldClassMember.strip();
            if (stripped.isEmpty()) continue;

            newClassMembers = newClassMembers
                    .and(() -> compileClassMember(stripped))
                    .mapValue(tuple -> tuple.left().add(tuple.right()));
        }
        return newClassMembers;
    }

    private static Result<String, CompileException> compileClassMember(String classMember) {
        return compileMethod(classMember)
                .orElseGet(() -> new Err<>(new CompileException("Invalid class member", classMember)));
    }

    private static Optional<Result<String, CompileException>> compileMethod(String classMember) {
        var paramStart = classMember.indexOf('(');
        if (paramStart == -1) return Optional.empty();

        var paramEnd = classMember.indexOf(')');
        if (paramEnd == -1) return Optional.empty();

        var definition = classMember.substring(0, paramStart).strip();
        var space = definition.lastIndexOf(" ");

        var modifiersAndType = definition.substring(0, space).strip();
        var typeSeparator = modifiersAndType.lastIndexOf(' ');
        if (typeSeparator == -1) return Optional.empty();

        var modifiersArray = modifiersAndType.substring(0, typeSeparator).strip().split(" ");
        var oldModifiers = Arrays.asList(modifiersArray);

        var name = definition.substring(space + 1).strip();
        var newModifiers = oldModifiers.contains("private") ? "private " : "";

        var params = classMember.substring(paramStart + 1, paramEnd);
        JavaList<String> content = new JavaList<>();
        return Optional.of(new Ok<>(MagmaLang.generateFunction(new Node(
                Map.of(Node.DEPTH, 2),
                Map.of(Node.MODIFIERS, newModifiers, Node.NAME, name, Node.PARAMS, params),
                Map.of(Node.CONTENT, content)
        ))));
    }

    private static Path resolve(String extension) {
        return Paths.get(".", "src", "magma", "Main." + extension);
    }

}
