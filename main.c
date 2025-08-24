/*package magma;*//*

import java.io.IOException;*//*
import java.nio.file.Files;*//*
import java.nio.file.Paths;*//*
import java.util.ArrayList;*//*
import java.util.stream.Collectors;*//*

public class Main {
	public static void main(String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "magma", "Main.java"));*//*
			Files.writeString(Paths.get(".", "main.c"), compile(input) + "int main(){\r\n\treturn 0;*//*\r\n}");*//*
			new ProcessBuilder("clang", "main.c", "-o", "main.exe").inheritIO().start().waitFor();*//*
		} catch (IOException | InterruptedException e) {
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

		return segments.stream().map(Main::wrap).collect(Collectors.joining());*//*
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";*//*
	}
}
*/int main(){
	return 0;
}