package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            String input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
            String output = compile(input);
            Files.writeString(Paths.get(".", "out", "main.c"), output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        ArrayList<String> segments = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            buffer.append(c);
            if (c == ';' && depth == 0) {
                segments.add(buffer.toString());
                buffer = new StringBuilder();
            } else {
                if (c == '{') depth++;
                if (c == '}') depth--;
            }
        }
        segments.add(buffer.toString());

        StringBuilder output = new StringBuilder();
        for (String segment : segments) {
            output.append(compileRootSegment(segment));
        }

        return output + "int main(){\n\treturn 0;\n}";
    }

    private static String compileRootSegment(String input) {
        if (input.startsWith("package ")) return "";
        if (input.strip().startsWith("import ")) return "#include <temp.h>\n";
        if (input.contains("class ")) return "struct Temp {\n};\n";

        System.err.println("Invalid root segment: " + input);
        return "";
    }
}
