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
			final Path target = source.resolveSibling("main.cpp");*//*
			Files.writeString(target, compile(input));*//*
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();*//*
		}
	}

	private static String compile(String input) {
		final ArrayList<String> segments = new ArrayList<String>();*//*
		StringBuilder buffer = new StringBuilder();*//*
		for (int index = 0;*//* index < input.length();*//* index++) {
			final char next = input.charAt(index);*//*
			buffer.append(next);*//*
			if (next == ';*//*') {
				segments.add(buffer.toString());*//*
				buffer = new StringBuilder();*//*
			}
		}
		segments.add(buffer.toString());*//*

		return segments.stream().map(Main::wrap).collect(Collectors.joining());*//*
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";*//*
	}
}*/