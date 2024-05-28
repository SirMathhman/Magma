package com.meti.compile;

import com.meti.api.result.Err;
import com.meti.api.result.Ok;
import com.meti.api.result.Result;

import java.util.ArrayList;
import java.util.Optional;

public record TypeCompiler(String inputType) {

    private static Optional<Result<String, CompileException>> compileGenericType(String inputType) {
        var genStart = inputType.indexOf('<');
        if (genStart == -1) return Optional.empty();

        var genEnd = inputType.lastIndexOf('>');
        if (genEnd == -1) return Optional.empty();

        var parent = inputType.substring(0, genStart);
        var childrenString = inputType.substring(genStart + 1, genEnd);
        var children = new ArrayList<String>();
        var builder = new StringBuilder();
        var depth = 0;
        for (int i = 0; i < childrenString.length(); i++) {
            var c = childrenString.charAt(i);
            if (c == ',' && depth == 0) {
                children.add(builder.toString());
                builder = new StringBuilder();
            } else {
                if (c == '<') depth++;
                if (c == '>') depth--;
                builder.append(c);
            }
        }
        children.add(builder.toString());

        var newChildren = new ArrayList<String>();
        for (String child : children) {
            var stripped = child.strip();
            if (stripped.isEmpty()) continue;

            try {
                final TypeCompiler typeCompiler = new TypeCompiler(stripped);
                newChildren.add(TypeCompiler.compile(typeCompiler.inputType).$());
            } catch (CompileException e) {
                return Optional.of(new Err<>(new CompileException("Failed to compile generic type: " + inputType, e)));
            }
        }

        return Optional.of(new Ok<>(parent + "<" + String.join(", ", newChildren) + ">"));
    }

    private static Optional<Result<String, CompileException>> compileSymbolType(String inputType) {
        if (Strings.isSymbol(inputType)) {
            return Optional.of(new Ok<>(inputType));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Result<String, CompileException>> compilePrimitiveType(String inputType) {
        if (inputType.equals("void")) {
            return Optional.of(new Ok<>("Void"));
        }
        if(inputType.equals("int")) {
            return Optional.of(new Ok<>("I32"));
        }
        return Optional.empty();
    }

    public static Result<String, CompileException> compile(String input) {
        return compilePrimitiveType(input)
                .or(() -> compileSymbolType(input))
                .or(() -> compileGenericType(input))
                .or(() -> TypeCompiler.compileArrayType(input))
                .orElseGet(() -> new Err<>(new CompileException("Unknown type: " + input)));
    }

    private static Optional<? extends Result<String, CompileException>> compileArrayType(String inputType) {
        if(inputType.endsWith("[]")) {
            var child = inputType.substring(0, inputType.length() - 2);
            return Optional.of(new Ok<>("&" + child));
        }
        return Optional.empty();
    }
}