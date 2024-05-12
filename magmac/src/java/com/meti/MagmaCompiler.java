package com.meti;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MagmaCompiler {
    static StackResult compileMagmaRoot(String line, String targetExtension) throws CompileException {
        return compileMagmaImport(line, targetExtension)
                .or(() -> compileMagmaFunction(line, targetExtension, 0, new LinkedList<>()))
                .orElseGet(() -> new Err<>(new CompileException(line)))
                .$();
    }

    static Optional<Result<StackResult, CompileException>> compileMagmaFunction(String line, String targetExtension, int indent, LinkedList<String> stack) {
        var stripped = line.strip();
        var functionKeyword = stripped.indexOf("def ");
        if (functionKeyword == -1) return Optional.empty();

        var isExported = stripped.startsWith("export ");
        var modifiers = List.of(stripped.substring(0, functionKeyword).strip().split(" "));
        var name = stripped.substring(functionKeyword + "def ".length(), stripped.indexOf('(')).strip();

        var contentStart = stripped.indexOf('{');
        var contentEnd = stripped.lastIndexOf('}');
        var content = stripped.substring(contentStart + 1, contentEnd);
        var inputContent = Splitter.split(content);

        var children = Collections.<String>emptyList();
        StackResult currentResult;
        try {
            currentResult = compileMagmaFunctionMembers(targetExtension, stack, inputContent, name).$();
        } catch (CompileException e) {
            return Optional.of(new Err<>(e));
        }

        var innerContent = currentResult.findInner().map(list -> list.stream()
                .map(Node::findValue)
                .flatMap(Optional::stream)
                .collect(Collectors.joining())).orElse("");
        var outerContent = currentResult.findOuter().map(list -> list.stream()
                .map(Node::findValue)
                .flatMap(Optional::stream)
                .collect(Collectors.joining())).orElse("");

        StackResult stackResult = renderMagmaFunction(targetExtension, isExported, name, innerContent, modifiers.contains("class"), indent, stack, children);
        var output = stackResult.withOuter(new Content(outerContent));

        return Optional.of(new Ok<>(output));
    }

    private static Result<StackResult, CompileException> compileMagmaFunctionMembers(String targetExtension, LinkedList<String> stack, List<String> inputContent, String name) {
        StackResult currentResult = new CompoundResult();
        for (String line : inputContent) {
            if (line.isBlank()) continue;

            try {
                stack.push(name);

                var result = compileMagmaFunctionMember(targetExtension, line, 1, stack);
                var inner = result.findInner().map(list -> list.stream()
                        .map(Node::findValue)
                        .flatMap(Optional::stream)
                        .collect(Collectors.joining())).orElse("");

                StackResult finalCurrentResult = currentResult;
                var withOuter = result.findOuter().map(list -> list.stream()
                        .map(Node::findValue)
                        .flatMap(Optional::stream)
                        .collect(Collectors.joining())).map(outer -> finalCurrentResult.withOuter(new Content(outer))).orElse(currentResult);
                currentResult = withOuter.withInner(new Content(inner));
            } catch (CompileException e) {
                return new Err<>(e);
            }
        }

        return new Ok<>(currentResult);
    }

    private static StackResult compileMagmaFunctionMember(String targetExtension, String input, int indent, LinkedList<String> stack) throws CompileException {
        return compileMagmaFunction(input, targetExtension, indent, stack)
                .orElseGet(() -> new Err<>(new CompileException(input)))
                .$();
    }

    static StackResult renderMagmaFunction(String targetExtension, boolean isExported, String name, String content, boolean isClass, int indent, LinkedList<String> stack, List<String> children) {
        if (targetExtension.equals("js")) {
            var exportedString = isExported ? "module.exports = {\n\t" + name + "\n}\n" : "";
            var childrenString = children.stream()
                    .map(child -> "\t" + child + "\n")
                    .collect(Collectors.joining());

            var classString = isClass ? "\treturn {\n" + childrenString + "\t};\n" : "";

            return new InnerResult("\t".repeat(indent) + "function " + name + "(){\n" + content + classString + "\t".repeat(indent) + "}\n" + exportedString);
        } else if (targetExtension.equals("d.ts")) {
            return new InnerResult(isExported ? "export function " + name + "() : {};" : "");
        } else {
            return transformMagmaFunctionToC(targetExtension, name, isExported, content, isClass, stack);
        }
    }

    static StackResult transformMagmaFunctionToC(
            String targetExtension,
            String name,
            boolean isExported,
            String content,
            boolean isClass,
            LinkedList<String> stack) {
        if (isClass) {
            var structType = "struct " + name + "_t";
            var structString = structType + " {\n}\n";
            if (targetExtension.equals("c")) {
                var structString1 = isExported ? "" : structString;
                return new InnerResult(structString1 + renderCFunction(structType, name, "\t" + structType + " this;\n" +
                                                                                         content +
                                                                                         "\treturn this;\n"));
            } else {
                return new InnerResult(isExported ? structString + structType + " " + name + "();\n" : "");
            }
        } else {
            if (targetExtension.equals("c")) {
                var afterName = IntStream.range(0, stack.size())
                        .map(index -> stack.size() - index - 1)
                        .mapToObj(stack::get)
                        .map(value -> "_" + value)
                        .collect(Collectors.joining());

                return new OuterResult(renderCFunction("void", name + afterName, ""));
            } else {
                return new EmptyResult();
            }
        }
    }

    private static String renderCFunction(String structType, String name, String content) {
        return structType + " " + name + "(){\n" + content + "}\n";
    }

    static Optional<Result<StackResult, CompileException>> compileMagmaImport(String line, String targetExtension) {
        var stripped = line.strip();
        if (!stripped.startsWith("import ")) return Optional.empty();

        var childStart = stripped.indexOf('{');
        var childEnd = stripped.indexOf('}');
        var child = stripped.substring(childStart + 1, childEnd).strip();
        var parent = stripped.substring(stripped.indexOf("from") + "from".length()).strip();
        var output = renderMagmaImport(targetExtension, child, parent);
        return Optional.of(new Ok<>(new InnerResult(output)));
    }

    static String renderMagmaImport(String targetExtension, String child, String parent) {
        if (targetExtension.equals("js") || targetExtension.equals("d.ts")) {
            return "import { " + child + " } from \"" + parent + "\";\n";
        } else if (targetExtension.equals("h")) {
            return "#include <" + parent + "/" + child + ".h>\n";
        } else {
            return "";
        }
    }
}