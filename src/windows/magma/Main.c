/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*
import java.util.Collection;*//*
import java.util.stream.Collectors;*//*

final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));*//*
			Files.writeString(Paths.get(".", "src", "windows", "magma", "Main.c"), Main.compile(input));*//*
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();*//*
		}
	}

	private static String compile(final CharSequence input) {
		final Collection<String> segments = new ArrayList<>();*//*
		final var buffer = new StringBuilder();*//*
		final var length = input.length();*//*
		for (var i = 0;*//* i < length;*//* i++) {
			final var c = input.charAt(i);*//*
			buffer.append(c);*//*
			if (';*//*' == c) {
				segments.add(buffer.toString());*//*
				buffer.setLength(0);*//*
			}
		}
		segments.add(buffer.toString());*//*

		return segments.stream().map(Main::wrapPlaceholder).collect(Collectors.joining());*//*
	}

	private static String wrapPlaceholder(final String input) {
		return "/*" + input + "*/";*//*
	}
}*/