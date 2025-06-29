/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*

public class Main {
    public static void main(final String[] args) {
        try {
            final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));*//*

            final var targetParent = Paths.get(".", "src", "node", "magma");*//*
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);*//*

            final var target = targetParent.resolve("Main.ts");*//*
            final var output = Main.compile(input);*//*
            Files.writeString(target, output);*//*
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();*//*
        }
    }

    private static String compile(final String input) {
        final var segments = new ArrayList<String>();*//*
        var buffer = new StringBuilder();*//*
        for (var i = 0;*//* i < input.length();*//* i++) {
            final var c = input.charAt(i);*//*
            buffer.append(c);*//*
            if (';*//*' == c) {
                segments.add(buffer.toString());*//*
                buffer = new StringBuilder();*//*
            }
        }
        segments.add(buffer.toString());*//*

        final var output = new StringBuilder();*//*
        for (final var segment : segments)
            output.append(generatePlaceholder(segment));*//*

        return output.toString();*//*
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("start", "start").replace("end", "end");*//*
        return "start" + replaced + "end";*//*
    }
}
*/