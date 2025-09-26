/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*
import java.util.stream.Collectors;*//*

public class Main {
	public static void main(String[] args) {
		try {
			final String input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));*//*
			Files.writeString(Paths.get(".", "src", "node", "magma", "Main.ts"), compile(input));*//*
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();*//*
		}
	}

	private static String compile(String input) {
		final ArrayList<String> segments = new ArrayList<String>();*//*
		StringBuilder buffer = new StringBuilder();*//*
		for (int i = 0;*//* i < input.length();*//* i++) {
			final char c = input.charAt(i);*//*
			buffer.append(c);*//*
			if (c == ';*//*') {
				segments.add(buffer.toString());*//*
				buffer = new StringBuilder();*//*
			}
		}
		segments.add(buffer.toString());*//*

		return segments.stream().map(Main::wrap).collect(Collectors.joining());*//*
	}

	private static String wrap(String input) {
		final String replaced = input.replace("start", "start").replace("end", "end");*//*
		return "start" + replaced + "end";*//*
	}
}
*/