package com.meti;

public record Compiler(String input) {
    String compile() {
        String output;
        if (input().isEmpty() || input().startsWith("package ")) {
            output = "";
        } else {
            var slice = input.substring("import ".length()).strip();

            var index = slice.indexOf(".");
            var parent = slice.substring(0, index).strip();
            var child = slice.substring(index + 1).strip();

            output = "import { " + child + " } from " + parent + ";";
        }
        return output;
    }
}