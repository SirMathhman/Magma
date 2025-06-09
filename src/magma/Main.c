/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*

public class Main {
    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");*//*
            final var input = Files.readString(source);*//*
            final var target = source.resolveSibling("Main.c");*//*

            final var segments = new ArrayList<String>();*//*
            var buffer = new StringBuilder();*//*
            for (var i = 0;*//* i < input.length();*//* i++) {
                final var c = input.charAt(i);*//*
                buffer.append(c);*//*
                if (c == ';*//*') {
                    segments.add(buffer.toString());*//*
                    buffer = new StringBuilder();*//*
                }
            }
            segments.add(buffer.toString());*//*

            final var output = new StringBuilder();*//*
            for (var segment : segments) {
                output.append(compileRootSegment(segment));*//*
            }

            Files.writeString(target, output.toString());*//*
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();*//*
        }
    }

    private static String compileRootSegment(String input) {
        return generatePlaceholder(input);*//*
    }

    private static String generatePlaceholder(String input) {
        return "start" + input
                .replace("start", "start")
                .replace("end", "end") + "end";*//*
    }
}
*/