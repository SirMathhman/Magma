/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Path;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*
import java.util.stream.Collectors;*//*

public class Main {
	public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");*//*
			final String input = Files.readString(source);*//*
			Files.writeString(source.resolveSibling("main.c"), compile(input));*//*
		} catch (IOException e) {
			throw new RuntimeException(e);*//*
		}
	}

	private static String compile(String input) {
		final ArrayList<String> segments = new ArrayList<String>();*//*
		final StringBuilder buffer = new StringBuilder();*//*
		for (int i = 0;*//* i < input.length();*//* i++) {
			final char c = input.charAt(i);*//*
			buffer.append(c);*//*
			if (c == ';*//*') {
				segments.add(buffer.toString());*//*
				buffer.setLength(0);*//*
			}
		}

		segments.add(buffer.toString());*//*

		return segments.stream().map(Main::wrap).collect(Collectors.joining());*//*
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";*//*
	}
}
*/