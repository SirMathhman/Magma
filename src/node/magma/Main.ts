/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Path;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*
import java.util.List;*//*
import java.util.stream.Collectors;*//*

public class Main {
    private Main() {}

    public static void main(final String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");*//*
        try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());*//*

            Main.runWithSources(sourceDirectory, sources);*//*
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();*//*
        }
    }

    private static void runWithSources(final Path sourceDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources) Main.runWithSource(sourceDirectory, source);*//*
    }

    private static void runWithSource(final Path sourceDirectory, final Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source.getParent());*//*

        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);*//*
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);*//*

        final var fileName = source.getFileName().toString();*//*
        final var separator = fileName.lastIndexOf('.');*//*
        final var name = fileName.substring(0, separator);*//*

        final var target = targetParent.resolve(name + ".ts");*//*
        final var input = Files.readString(source);*//*

        final var segments = Main.divide(input);*//*

        final var output = new StringBuilder();*//*
        for (final var segment : segments) output.append(Main.generatePlaceholder(segment));*//*

        Files.writeString(target, output.toString());*//*
    }

    private static List<String> divide(final CharSequence input) {
        final List<String> segments = new ArrayList<>();*//*
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
        return segments;*//*
    }

    private static String generatePlaceholder(final String input) {
        return "start" + input.replace("start", "start").replace("end", "end") + "end";*//*
    }
}
*/