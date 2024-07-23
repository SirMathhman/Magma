package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record Compiler(String input) {
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";

    private static String generate(List<String> modified) {
        var output = new StringBuilder();
        for (int i = 0; i < modified.size(); i++) {
            var segment = modified.get(i);
            var prefix = i == 0 ? "" : "\n";
            output.append(prefix).append(segment);
        }

        return output.toString();
    }

    private static String compileRootMember(String rootMember) throws CompileException {
        if (rootMember.startsWith("package ")) return "";
        if (rootMember.startsWith("import ")) return rootMember;

        return compileClass(rootMember).orElseThrow(() -> new CompileException("Unknown root member", rootMember));
    }

    private static Optional<String> compileClass(String rootMember) throws CompileException {
        var classIndex = rootMember.indexOf(CLASS_KEYWORD_WITH_SPACE);
        if (classIndex == -1) return Optional.empty();

        var oldModifiers = rootMember.substring(0, classIndex);
        var newModifiers = oldModifiers.equals("public ") ? "export " : "";

        var right = rootMember.substring(classIndex + CLASS_KEYWORD_WITH_SPACE.length());

        var contentStart = right.indexOf('{');
        if (contentStart == -1) return Optional.empty();

        var name = right.substring(0, contentStart).strip();
        var contentAndEnd = right.substring(contentStart + 1).strip();
        if (!contentAndEnd.endsWith("}")) return Optional.empty();

        var content = contentAndEnd.substring(0, contentAndEnd.length() - 1);
        var inputClassMembers = Splitter.split(content);
        var outputClassMembers = new ArrayList<String>();
        for (var inputClassMember : inputClassMembers) {
            outputClassMembers.add(compileClassMember(inputClassMember.strip()));
        }

        var generated = generate(outputClassMembers);
        return Optional.of(newModifiers + CLASS_KEYWORD_WITH_SPACE + "def " + name + "() => {" + generated + "}");
    }

    private static String compileClassMember(String classMember) throws CompileException {
        return compileDeclaration(classMember)
                .or(() -> compileMethod(classMember))
                .orElseGet(() -> new Err<>(new CompileException("Invalid class member", classMember)))
                .unwrap();
    }

    private static Optional<Result<String, CompileException>> compileMethod(String classMember) {
        var contentStart = classMember.indexOf('{');
        if (contentStart == -1) return Optional.empty();

        var definition = classMember.substring(0, contentStart).strip();
        var compiledDefinition = compileDefinition(definition.strip());
        return compiledDefinition.map(Ok::new);

    }

    private static Optional<Result<String, CompileException>> compileDeclaration(String classMember) {
        var separator = classMember.indexOf('=');
        if (separator == -1) return Optional.empty();

        var definitionOptional = compileDefinition(classMember.substring(0, separator).strip());
        if (definitionOptional.isEmpty()) return Optional.empty();

        var valueAndEnd = classMember.substring(separator + 1).strip();
        if (!valueAndEnd.endsWith(";")) return Optional.empty();

        var value = valueAndEnd.substring(0, valueAndEnd.length() - 1).strip();
        return Optional.of(compileValue(value).mapValue(compiledValue -> definitionOptional.get() + " = " + compiledValue + ";"));
    }

    private static Optional<String> compileDefinition(String definition) {
        var last = definition.lastIndexOf(' ');
        if (last == -1) return Optional.empty();

        var name = definition.substring(last + 1).strip();
        return Optional.of("let " + name);
    }

    private static Result<String, CompileException> compileValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) return new Ok<>(value);
        return new Err<>(new CompileException("Unknown value", value));
    }

    String compile() throws CompileException {
        var segments = Splitter.split(input());

        var rootMembers = new ArrayList<String>();
        for (String segment : segments) {
            var stripped = segment.strip();
            if (stripped.isEmpty()) continue;
            rootMembers.add(compileRootMember(stripped));
        }

        var modified = new ArrayList<String>();
        for (String segment : rootMembers) {
            if (segment.isEmpty()) continue;
            modified.add(segment);
        }

        return generate(modified);
    }
}