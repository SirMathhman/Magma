/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*
import java.util.stream.Collectors;*//*

public class Main {
	public static void main(String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");*//*
		try (var stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());*//*

			for (var source : sources) {
				final var input = Files.readString(source);*//*

				final var relative = sourceDirectory.relativize(source);*//*
				final var parent = relative.getParent();*//*
				final var segments = new ArrayList<String>();*//*
				for (var i = 0;*//* i < parent.getNameCount();*//* i++) {
					segments.add(parent.getName(i).toString());*//*
				}

				var targetDirectory = Paths.get(".", "src", "windows");*//*
				for (var segment : segments) {
					targetDirectory = targetDirectory.resolve(segment);*//*
				}

				Files.createDirectories(targetDirectory);*//*

				final var fileName = relative.getFileName().toString();*//*
				final var index = fileName.lastIndexOf(".");*//*
				final var name = fileName.substring(0, index);*//*
				final var resolve = targetDirectory.resolve(name + ".c");*//*
				Files.writeString(resolve, compile(input));*//*
			}
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();*//*
		}
	}

	private static String compile(String input) {
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

		return segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());*//*
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";*//*
	}

	private static String compileRootSegment(String input) {
		return wrap(input);*//*
	}
}
*/