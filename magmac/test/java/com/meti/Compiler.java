package com.meti;

import java.util.*;
import java.util.stream.Collectors;

public class Compiler {
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String STATEMENT_END = ";";
    public static final String STATIC_KEYWORD_WITH_SPACE = "static ";
    public static final String BLOCK_START = "{";
    public static final String BLOCK_END = "}";
    public static final String INT_KEYWORD = "int";
    public static final String I32_KEYWORD = "I32";
    public static final String LONG_KEYWORD = "long";
    public static final String I64_KEYWORD = "I64";
    public static final String VALUE_SEPARATOR = "=";

    public static String renderJavaDefinition(String type, String name, String value) {
        return renderJavaDefinition("", type, name, value);
    }

    public static String renderJavaDefinition(String modifiersString, String type, String name, String value) {
        return modifiersString + type + " " + name + " " + VALUE_SEPARATOR + " " + value + ";";
    }

    public static String renderMagmaDefinition(String name, String type, String value) {
        return renderMagmaDefinition("", name, type, value);
    }

    public static String renderMagmaDefinition(String modifierString, String name, String type, String value) {
        return modifierString + "let " + name + " : " + type + " " + VALUE_SEPARATOR + " " + value + ";";
    }

    private static String renderBlock(String content) {
        return BLOCK_START + content + BLOCK_END;
    }

    static String renderMagmaImport(String parent, String child) {
        return IMPORT_KEYWORD_WITH_SPACE + "{ " + child + " } from " + parent + STATEMENT_END;
    }

    static String renderMagmaFunction(String name) {
        return renderMagmaFunction("", name);
    }

    static String renderMagmaFunction(String modifiersString, String name) {
        return renderMagmaFunction(modifiersString, name, "");
    }

    static String renderMagmaFunction(String modifiersString, String name, String content) {
        return modifiersString + CLASS_KEYWORD_WITH_SPACE + "def " + name + "() =>" + " " + renderBlock(content);
    }

    static String run(String input) {
        var lines = split(input);
        var imports = lines.subList(0, lines.size() - 1);
        var classString = lines.get(lines.size() - 1);

        var beforeString = imports.stream()
                .map(Compiler::compileImport)
                .collect(Collectors.joining());

        var compiledClass = compileClass(classString);
        return beforeString + compiledClass;
    }

    private static ArrayList<String> split(String input) {
        var lines = new ArrayList<String>();
        var builder = new StringBuilder();
        var depth = 0;
        for (int i = 0; i < input.length(); i++) {
            var c = input.charAt(i);
            if (c == ';' && depth == 0) {
                lines.add(builder.toString());
                builder = new StringBuilder();
            } else {
                if (c == '{') depth++;
                if (c == '}') depth--;
                builder.append(c);
            }
        }

        lines.add(builder.toString());
        return lines;
    }

    private static String compileImport(String beforeString) {
        if (!beforeString.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return "";
        var segmentStart = beforeString.startsWith(IMPORT_KEYWORD_WITH_SPACE + STATIC_KEYWORD_WITH_SPACE)
                ? (IMPORT_KEYWORD_WITH_SPACE + STATIC_KEYWORD_WITH_SPACE).length()
                : IMPORT_KEYWORD_WITH_SPACE.length();

        var set = beforeString.substring(segmentStart);
        var last = set.lastIndexOf('.');
        var parent = set.substring(0, last);
        var child = set.substring(last + 1);

        return renderMagmaImport(parent, child);
    }

    private static String compileClass(String input) {
        var classIndex = input.indexOf(CLASS_KEYWORD_WITH_SPACE);
        if (classIndex == -1) throw new UnsupportedOperationException("No class present.");

        var nameStart = classIndex + CLASS_KEYWORD_WITH_SPACE.length();
        var contentStart = input.indexOf(BLOCK_START);
        var contentEnd = input.lastIndexOf(BLOCK_END);

        var className = input.substring(nameStart, contentStart).strip();
        var modifierString = input.startsWith(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";

        var inputContent = input.substring(contentStart + 1, contentEnd);
        var outputContent = compileDefinition(inputContent);

        return renderMagmaFunction(modifierString, className, outputContent);
    }

    private static String compileDefinition(String inputContent) {
        var valueSeparatorIndex = inputContent.indexOf(VALUE_SEPARATOR);
        if (valueSeparatorIndex == -1) return "";

        var before = inputContent.substring(0, valueSeparatorIndex).strip();
        var separator = before.lastIndexOf(' ');

        var name = before.substring(separator + 1);

        var modifiersAndType = before.substring(0, separator);

        Set<String> modifiers;
        String inputType;

        var lastIndex = modifiersAndType.indexOf(' ');
        if (lastIndex == -1) {
            inputType = modifiersAndType;
            modifiers = Collections.emptySet();
        } else {
            var modifiersString = modifiersAndType.substring(0, lastIndex).strip();
            var typeString = modifiersAndType.substring(lastIndex + 1).strip();

            modifiers = new HashSet<>(Arrays.asList(modifiersString.split(" ")));
            inputType = typeString;
        }

        var outputType = compileType(inputType);

        var after = inputContent.substring(valueSeparatorIndex + 1, inputContent.lastIndexOf(STATEMENT_END)).strip();
        var modifierString = modifiers.isEmpty() ? "" : modifiers.stream().map(modifier -> modifier + " ").collect(Collectors.joining());

        return renderMagmaDefinition(modifierString, name, outputType, after);
    }

    private static String compileType(String inputType) {
        String outputType;
        if (inputType.equals(INT_KEYWORD)) {
            outputType = I32_KEYWORD;
        } else if (inputType.equals(LONG_KEYWORD)) {
            outputType = I64_KEYWORD;
        } else {
            outputType = inputType;
        }
        return outputType;
    }

    static String renderJavaClass(String name) {
        return renderJavaClass("", name);
    }

    static String renderJavaClass(String modifiersString, String name) {
        return renderJavaClass(modifiersString, name, "");
    }

    static String renderJavaClass(String modifiersString, String name, String content) {
        return modifiersString + CLASS_KEYWORD_WITH_SPACE + name + " " + renderBlock(content);
    }

    static String renderJavaImport(String parent, String child) {
        return renderJavaImport(parent, child, "");
    }

    static String renderJavaImport(String parent, String child, String modifierString) {
        return IMPORT_KEYWORD_WITH_SPACE + modifierString + parent + "." + child + STATEMENT_END;
    }

    static String renderBeforeFunction(String before) {
        return before + ApplicationTest.renderMagmaFunction();
    }
}
