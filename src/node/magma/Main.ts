/*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.List;*//*

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");*//*
            final var input = Files.readString(source);*//*
            final var segments = Main.divide(input);*//*

            final var output = new StringBuilder();*//*
            for (final var segment : segments)
                output.append(Main.compileRootSegment(segment));*//*

            final var targetParent = Paths.get(".", "src", "node", "magma");*//*
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);*//*

            final var target = targetParent.resolve("Main.ts");*//*
            Files.writeString(target, output.toString());*//*
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();*//*
        }
    }

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();*//*
        if (strip.startsWith("package "))
            return "";*//*


        return Main.generatePlaceholder(input);*//*
    }

    private static List<String> divide(final CharSequence input) {
        final State state = new MutableState();*//*
        final var length = input.length();*//*
        var current = state;*//*
        for (var i = 0;*//* i < length;*//* i++) {
            final var c = input.charAt(i);*//*
            current = Main.fold(current, c);*//*
        }

        return current.advance()
                .unwrap();*//*
    }

    private static State fold(final State state, final char c) {
        final var appended = state.append(c);*//*
        if (';*//*' == c)
            return appended.advance();*//*
        return appended;*//*
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("start", "start")
                .replace("end", "end");*//*

        return "start" + replaced + "end";*//*
    }
}
*/