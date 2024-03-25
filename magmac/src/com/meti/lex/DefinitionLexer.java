package com.meti.lex;

import com.meti.node.Content;
import com.meti.DefinitionNode;
import com.meti.TypeCompiler;

import java.util.*;
import java.util.stream.Collectors;

public record DefinitionLexer(String body) {

    public Optional<DefinitionNode> lex() {
        var valueSeparator = body().indexOf('=');
        if (valueSeparator == -1) return Optional.empty();

        var before = body().substring(0, valueSeparator).strip();
        var after = body().substring(valueSeparator + 1).strip();

        var nameSeparator = before.lastIndexOf(' ');
        if (nameSeparator == -1) return Optional.empty();

        var keys = before.substring(0, nameSeparator).strip();
        var typeSeparator = keys.lastIndexOf(' ');
        var type = typeSeparator == -1 ? keys : keys.substring(typeSeparator + 1).strip();

        Set<String> inputFlags;
        if (typeSeparator == -1) {
            inputFlags = Collections.emptySet();
        } else {
            inputFlags = Arrays.stream(keys.substring(0, typeSeparator).strip().split(" "))
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .collect(Collectors.toSet());
        }

        var outputFlags = new ArrayList<String>();
        if (inputFlags.contains("public")) {
            outputFlags.add("pub");
        }

        if (inputFlags.contains("final")) {
            outputFlags.add("const");
        } else {
            outputFlags.add("let");
        }

        var outputType = new TypeCompiler(type).compile();

        var name = before.substring(nameSeparator + 1).strip();
        return Optional.of(new DefinitionNode(outputFlags, name, outputType, new Content(after, 0)));
    }

}