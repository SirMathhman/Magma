package com.meti;

import java.util.ArrayList;

public class Compiler {
    public static final String IMPORT_KEYWORD = "import ";
    public static final String STATIC_KEYWORD = "static ";
    public static final String IMPORT_STATIC = IMPORT_KEYWORD + STATIC_KEYWORD;

    static String compile(String input) {
        var args = input.split(";");
        var list = new ArrayList<String>();
        for (String arg : args) {
            list.add(compileRootStatement(arg.strip()));
        }
        return String.join("", list);
    }

    private static String compileRootStatement(String input) {
        if (input.startsWith(IMPORT_KEYWORD)) {
            var isStatic = input.startsWith(IMPORT_STATIC);
            var importKeyword = isStatic ? IMPORT_STATIC : IMPORT_KEYWORD;
            var segmentsString = input.substring(importKeyword.length());

            var separator = segmentsString.lastIndexOf('.');
            var parent = segmentsString.substring(0, separator);
            var child = segmentsString.substring(separator + 1);

            return child.equals("*")
                    ? renderMagmaImportForAllChildren(parent)
                    : renderMagmaImport(parent, child);
        } else if (input.startsWith("class ")) {
            var name = input.substring("class ".length(), input.indexOf(" {}"));
            return renderMagmaClass(name);
        } else {
            return "";
        }
    }

    static String renderMagmaImport(String parent, String child) {
        return renderMagmaImportWithChildString(parent, "{ " + child + " } from ");
    }

    static String renderMagmaImportForAllChildren(String parent) {
        return renderMagmaImportWithChildString(parent, "");
    }

    private static String renderMagmaImportWithChildString(String parent, String childString) {
        return "extern " + IMPORT_KEYWORD + childString + parent + ";\n";
    }

    static String renderMagmaClass(String name) {
        return "class def " + name + "() => {}";
    }

    static String renderJavaClass(String name) {
        return "class " + name + " {}";
    }
}